package com.example.Magpie.Nest;

import org.apache.coyote.Response;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

@Controller
@RequestMapping("/Magpie")
public class PageServlet
{
    String downloadDir = System.getProperty("java.io.tmpdir") + "/magpie-videos";
    File folder = new File(downloadDir);

    @GetMapping
    protected String loadPage()
    {
        return "DownloadPage";
    }

    //TODO: ADD OTHER LANGUAGE SUPPORTS
    @GetMapping("/downloadVideo")
    @ResponseBody
    protected ResponseEntity<Resource> downloadVideo(@RequestParam String link)
    {
        if(link.startsWith("https://www.youtube.com/watch?v"))
        {
            try
            {
                if(!folder.exists()) folder.mkdirs();

                ProcessBuilder processBuilder = new ProcessBuilder("yt-dlp", "-f", "best", link);
                processBuilder.directory(new File(downloadDir));
                Process process = processBuilder.start();

                int exitCode = process.waitFor();

                if (exitCode != 0)
                {
                    return ResponseEntity.internalServerError().build();
                }

                File[] files = folder.listFiles();

                if (files == null || files.length == 0)
                {
                    return ResponseEntity.notFound().build();
                }

                File latest = Arrays.stream(files).filter(File::isFile).max(Comparator.comparingLong(File::lastModified)).orElseThrow();

                Path path = latest.toPath();
                Resource resource = new UrlResource(path.toUri());

                String encodedFileName = java.net.URLEncoder.encode(latest.getName(), StandardCharsets.UTF_8)
                        .replaceAll("\\+", "%20");

                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                        .body(resource);
            }
            catch (Exception e)
            {
                return ResponseEntity.internalServerError().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }
}






