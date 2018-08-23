package org.zalando.zmon.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.zalando.zmon.domain.Alert;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping(value = "/rest")
public class DnsCache {

    private final Logger log = LoggerFactory.getLogger(DnsCache.class);

    @RequestMapping(value = "/dumpcache", method = RequestMethod.GET)
    public ResponseEntity<String> dumpCache(@RequestParam(value = "domain", required = false) final String domain) throws Exception{

        InetAddress.getByName("stackoverflow.com");
        InetAddress.getByName("www.google.com");
        InetAddress.getByName("www.yahoo.com");
        InetAddress.getByName("www.example.com");
        InetAddress.getByName(domain);
        try {
            InetAddress.getByName("nowhere.example.com");
        } catch (UnknownHostException e) {

        }

        String addressCache = "addressCache";
        log.info(addressCache);
        printDNSCache(addressCache);
        String negativeCache = "negativeCache";
        log.info(negativeCache);
        printDNSCache(negativeCache);

        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }

    private void printDNSCache(String cacheName) throws Exception {
        Class<InetAddress> klass = InetAddress.class;
        Field acf = klass.getDeclaredField(cacheName);
        acf.setAccessible(true);
        Object addressCache = acf.get(null);
        Class cacheKlass = addressCache.getClass();
        Field cf = cacheKlass.getDeclaredField("cache");
        cf.setAccessible(true);
        Map<String, Object> cache = (Map<String, Object>) cf.get(addressCache);
        for (Map.Entry<String, Object> hi : cache.entrySet()) {

            Object cacheEntry = hi.getValue();
            Class cacheEntryKlass = cacheEntry.getClass();
            Field expf = cacheEntryKlass.getDeclaredField("expiration");
            expf.setAccessible(true);
            long expires = (Long) expf.get(cacheEntry);

            Field af = cacheEntryKlass.getDeclaredField("addresses");
            af.setAccessible(true);
            InetAddress[] addresses = (InetAddress[]) af.get(cacheEntry);
            List<String> ads = new ArrayList<String>(addresses.length);
            for (InetAddress address : addresses) {
                ads.add(address.getHostAddress());
            }

            log.info(hi.getKey() + " expires in "
                    + Instant.now().until(Instant.ofEpochMilli(expires), ChronoUnit.SECONDS) + " seconds " + ads);
        }
    }
}
