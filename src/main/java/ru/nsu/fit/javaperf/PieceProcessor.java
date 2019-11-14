package ru.nsu.fit.javaperf;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PieceProcessor implements Runnable {
    private PieceProducer pieceProducer;
    private int buffsize;
    private int length;
    private byte[] buffer;
    private Pattern pattern;
    private String template;
    private long startTime = System.currentTimeMillis();

    public PieceProcessor(PieceProducer pieceProducer, String pattern, int buffsize) {
        this.pieceProducer = pieceProducer;
        var quotedString = Pattern.quote(pattern);
        this.pattern = Pattern.compile("(.*)" + quotedString + "(.*)");
        this.buffsize = buffsize;
        this.length = pattern.length();
        this.template = pattern;
        buffer = new byte[this.buffsize];
    }

    @Override
    public void run() {
        var piece = pieceProducer.get();
        while (true) {
            try {
                processPiece(piece);
                piece = pieceProducer.get();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void processPiece(Piece piece) throws IOException {
        try (var randomAccessFile = new RandomAccessFile(piece.file, "r")) {
            randomAccessFile.seek(piece.offset);
            int read = randomAccessFile.read(buffer);
            if (read < length) {
                return;
            }

            String input = new String(buffer, 0, read, StandardCharsets.US_ASCII);
            checkByMatcher(piece, input);
            //checkByIndex(piece, input);
        }
    }

    private void checkByMatcher(Piece piece, String input) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            int start = matcher.start(2);
            System.out.println("found in" + piece.file.getName() + (piece.offset + start));
            System.out.println("time:" + (System.currentTimeMillis() - startTime)/1000.);
        }
    }

    private void checkByIndex(Piece piece, String input){
        var a = input.indexOf(template);
        if(a!=-1){
            System.out.println("found in" + piece.file.getName() + (piece.offset + a));
            System.out.println("time:" + (System.currentTimeMillis() - startTime)/1000.);
        }
    }
}
