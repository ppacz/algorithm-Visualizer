package utils;

public class Counter {
    private int count;
    
    public Counter(){
        this.count = 0;
    }

    public int getCount() {
        return count;
    }

    public void increment() {
        this.count++;
    }
}
