package com.example.zero2dev.services;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class IpService {

    public static String getClientIp(HttpServletRequest request) {
        String[] IP_HEADERS = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_CLIENT_IP",
                "HTTP_X_CLUSTER_CLIENT_IP"
        };

        for (String header : IP_HEADERS) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0];
            }
        }

        return request.getRemoteAddr();
    }
    public static String generateDeviceInfoString(HttpServletRequest request) {
        if (request == null) {
            return "Unknown|Unknown|Unknown|Unknown|" + System.currentTimeMillis();
        }

        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIp(request);

        try {
            if (userAgent == null) {
                userAgent = "Unknown";
            }

            UserAgent parsedUserAgent = UserAgent.parseUserAgentString(userAgent);
            Browser browser = parsedUserAgent.getBrowser();
            OperatingSystem os = parsedUserAgent.getOperatingSystem();
            Version browserVersion = parsedUserAgent.getBrowserVersion();

            String osFamily = os.getDeviceType().getName(); // Loại thiết bị (Desktop, Mobile, Tablet)
            String osManufacturer = os.getManufacturer().getName(); // Nhà sản xuất OS
            String browserType = browser.getBrowserType().getName(); // Loại trình duyệt (Web Browser, Mobile Browser)
            String browserManufacturer = browser.getManufacturer().getName(); // Nhà sản xuất trình duyệt

            return String.format(
                    "OS:%s|OSFamily:%s|OSManufacturer:%s|" +
                            "Browser:%s|BrowserType:%s|BrowserManufacturer:%s|" +
                            "BrowserVersion:%s|IP:%s|Timestamp:%d",
                    os.getName(),
                    osFamily,
                    osManufacturer,
                    browser.getName(),
                    browserType,
                    browserManufacturer,
                    browserVersion != null ? browserVersion.getVersion() : "Unknown",
                    ipAddress != null ? ipAddress : "Unknown",
                    System.currentTimeMillis()
            );
        } catch (Exception e) {
            return String.format(
                    "Parse Error|UserAgent:%s|IP:%s|Timestamp:%d",
                    userAgent,
                    ipAddress != null ? ipAddress : "Unknown",
                    System.currentTimeMillis()
            );
        }
    }
}
