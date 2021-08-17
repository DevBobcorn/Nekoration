package com.devbobcorn.nekoration.utils;

public class PixelPos{
    public final int x;
    public final int y;

    public PixelPos(int x ,int y){
        this.x = x;
        this.y = y;
    }

    public PixelPos up(){
        return new PixelPos(x, y + 1);
    }

    public PixelPos down(){
        return new PixelPos(x, y - 1);
    }

    public PixelPos left(){
        return new PixelPos(x - 1, y);
    }

    public PixelPos right(){
        return new PixelPos(x + 1, y);
    }

    public PixelPos offset(int dx, int dy){
        return new PixelPos(x + dx, y + dy);
    }
}