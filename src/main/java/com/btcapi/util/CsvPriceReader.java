package com.btcapi.util;

import com.btcapi.model.PriceRecord;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CsvPriceReader {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String resourcePath;

    public CsvPriceReader() {
        this("/static/BTCUSD_H1.csv");
    }

    public CsvPriceReader(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public List<PriceRecord> readAll() throws IOException {
        return read(Optional.empty(), Optional.empty());
    }

    public List<PriceRecord> read(Optional<LocalDate> optIniDateFilter, Optional<LocalDate> optFinDateFilter) throws IOException {
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new FileNotFoundException("Resource not found on path: " + resourcePath);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            br.readLine();
            List<PriceRecord> result = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cols = line.split("\\t");
                if (cols.length < 6) continue;

                LocalDateTime time = LocalDateTime.parse(cols[0].trim(), FORMATTER);
                double open = Double.parseDouble(cols[1].trim());
                double high = Double.parseDouble(cols[2].trim());
                double low = Double.parseDouble(cols[3].trim());
                double close = Double.parseDouble(cols[4].trim());

                long volume;
                try {
                    volume = Long.parseLong(cols[5].trim());
                } catch (NumberFormatException e) {
                    volume = (long) Double.parseDouble(cols[5].trim());
                }

                if(optIniDateFilter.isPresent()) {
                    LocalDate iniDateFilter = optIniDateFilter.get();

                    if (time.toLocalDate().isBefore(iniDateFilter)) {
                        continue;
                    }
                }

                if(optFinDateFilter.isPresent()) {
                    LocalDate finDateFilter = optFinDateFilter.get();

                    if (time.toLocalDate().isAfter(finDateFilter)) {
                        continue;
                    }
                }

                result.add(new PriceRecord(time, open, high, low, close, volume));
            }
            return result;
        }
    }
}
