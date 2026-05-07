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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

@Controller
@RequestMapping("/Magpie")
public class PageServlet
{
    @GetMapping
    protected String loadPage()
    {
        return "DownloadPage";
    }

    //TODO: ADD OTHER LANGUAGE SUPPORTS
    @GetMapping("/downloadVideo")
    @ResponseBody
    protected ResponseEntity<Resource> downloadVideo(@RequestParam String link) throws IOException, InterruptedException
    {
        String downloadDir = "C:\\Users\\lasha\\OneDrive\\Documents\\Videos";
        if(link.startsWith("https://www.youtube.com/watch?v"))
        {
            ProcessBuilder processBuilder = new ProcessBuilder("yt-dlp", "-f", "best", link);
            processBuilder.directory(new File(downloadDir));
            processBuilder.start().waitFor();

            File folder = new File(downloadDir);

            File[] files = folder.listFiles();

            File latest = Arrays.stream(files).filter(File::isFile).max(Comparator.comparingLong(File::lastModified)).orElseThrow();

            Path path = latest.toPath();
            Resource resource = new UrlResource(path.toUri());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                            + latest.getName() + "\"")
                    .body(resource);
        }
        return null;
    }
}






