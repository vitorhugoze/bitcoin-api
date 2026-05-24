package com.btcapi.model;

import java.time.LocalDateTime;

public class PriceRecord {
    private final LocalDateTime time;
    private final double open;
    private final double high;
    private final double low;
    private final double close;
    private final long volume;

    public PriceRecord(LocalDateTime time, double open, double high, double low, double close, long volume) {
        this.time = time;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public LocalDateTime getTime() { return time; }
    public double getOpen() { return open; }
    public double getHigh() { return high; }
    public double getLow() { return low; }
    public double getClose() { return close; }
    public long getVolume() { return volume; }

    @Override
    public String toString() {
        return "PriceRecord{" +
                "time=" + time +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                '}';
    }
}
