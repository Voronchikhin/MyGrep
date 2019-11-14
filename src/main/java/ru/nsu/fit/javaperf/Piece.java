package ru.nsu.fit.javaperf;

import java.io.File;

public class Piece {
    public Piece(File file, long offset, long buffsize) {
        this.file = file;
        this.offset = offset;
        this.buffsize = buffsize;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "offset=" + offset +
                ", buffsize=" + buffsize +
                '}';
    }

    public File file;
    public long offset;
    public long buffsize;
}
