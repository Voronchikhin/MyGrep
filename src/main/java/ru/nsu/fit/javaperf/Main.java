package ru.nsu.fit.javaperf;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

        Options options = new Options();
        options.addOption("data", "data", true, "search occurrence in file");
        options.addOption("file", "file", true, "search occurrence in filenames");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine commandLine = parser.parse(options, args);
            boolean data = commandLine.hasOption("data");
            System.out.println("data = " + data);
            var pattern = commandLine.getOptionValue("file");
            System.out.println("pattern = " + pattern);
            boolean file = commandLine.hasOption("file");
            System.out.println("file = " + file);
            List<String> fileNames = commandLine.getArgList();
            System.out.println("fileNames = " + fileNames);
            if (file) {
                for (String fileName : fileNames) {
                    Files.walkFileTree(Path.of(fileName), new NameFileVisitor(commandLine.getOptionValue("file")));
                }
            } else if (data) {
                pattern = commandLine.getOptionValue("data");
                for (String fileName: fileNames){
                    int buffsize = 32000;
                    PieceProducer pieceProducer = new PieceProducer(buffsize,new File(fileName),pattern.length());
                    var threadPool = Executors.newFixedThreadPool(6);
                    var pieceProcessors = new ArrayList<PieceProcessor>(6);
                    for (int i = 0; i < 6; i++) {
                        pieceProcessors.add(new PieceProcessor(pieceProducer,pattern,buffsize));
                    }
                    pieceProcessors.forEach(threadPool::submit);
                }
            }
        } catch (ParseException | IOException e) {
            new HelpFormatter().printHelp("[options] <FILE>", options);
        }
    }

    static class NameFileVisitor extends SimpleFileVisitor<Path> {
        private String pattern;

        NameFileVisitor(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (file.toString().contains(pattern)) {
                System.out.println("find file:" + file);
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
