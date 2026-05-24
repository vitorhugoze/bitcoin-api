package com.btcapi.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.btcapi.model.PriceRecord;
import com.btcapi.util.CsvPriceReader;

@RestController
public class BtcapiController {

    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("teste!");
    }

    @GetMapping("/prices")
    public ResponseEntity<List<PriceRecord>> getBtcPrices(
            @RequestHeader(value = "iniDate", required = false) String iniDate,
            @RequestHeader(value = "finDate", required = false) String finDate) {
        Optional<LocalDate> optIniDate = Optional.empty();
        Optional<LocalDate> optFinDate = Optional.empty();

        try {
            if (iniDate != null && !iniDate.trim().isEmpty()) {
                optIniDate = Optional.of(LocalDate.parse(iniDate));
            }

            if (finDate != null && !finDate.trim().isEmpty()) {
                optFinDate = Optional.of(LocalDate.parse(finDate));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest().body(List.of());
        }

        try {
            CsvPriceReader reader = new CsvPriceReader();
            var allRecords = reader.read(optIniDate, optFinDate);

            return ResponseEntity.ok(allRecords);
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of());
        }
    }

}
