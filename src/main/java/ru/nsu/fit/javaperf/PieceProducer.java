package ru.nsu.fit.javaperf;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class PieceProducer implements Supplier<Piece> {
    private ReentrantLock mutex = new ReentrantLock();
    private long offset = 0L;
    private long buffsize;
    private File file;
    private int patternSize;

    public PieceProducer(long buffsize, File file, int patternSize) {
        assert buffsize > patternSize;
        this.buffsize = buffsize;
        this.file = file;
        this.patternSize = patternSize;
    }

    @Override
    public Piece get() {
        mutex.lock();
        Piece piece = createPiece();
        mutex.unlock();
        return piece;
    }

    private Piece createPiece() {
        Piece piece = null;
        if (offset < file.length()) {
            piece = new Piece(file, offset, buffsize);
        }
        offset += buffsize - patternSize + 1;
        return piece;
    }
}
