package com.example.Magpie.Nest;

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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

@Controller
@RequestMapping("/")
public class PageServlet
{
    String downloadDir = System.getProperty("java.io.tmpdir") + "/magpie-videos";
    File folder = new File(downloadDir);
    String cookies = new File("www.youtube.com_cookies.txt").getAbsolutePath();

    String bestFallback = "/best";
    String capFormat = "bestvideo[height<=%s][ext=mp4]+bestaudio[ext=m4a]/best[height<=%s][ext=mp4]";

    @GetMapping
    protected String redirect() { return "redirect:/Magpie"; }

    @GetMapping("Magpie")
    protected String loadPage()
    {
        return "DownloadPage";
    }

    @GetMapping("Magpie/downloadVideo")
    @ResponseBody
    protected ResponseEntity<Resource> downloadVideo(@RequestParam String link, @RequestParam String quality)
    {
        if(link.startsWith("https://www.youtube.com/watch?v"))
        {
            try
            {
                System.out.println("received");
                if(!folder.exists()) folder.mkdirs();

                /*
                ProcessBuilder processBuilder = new ProcessBuilder("yt-dlp", "--no-playlist",
                        "--force-ipv4",
                        "-f", "bestvideo[protocol^=https]+bestaudio/best[protocol^=https]",
                        "--merge-output-format", "mp4", link);

                 */
                
                String qualityCom;
                if(quality.equals("best")) qualityCom = "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]";
                else qualityCom = String.format(capFormat, quality, quality);

                qualityCom += bestFallback;

                ProcessBuilder processBuilder = new ProcessBuilder("yt-dlp", "-f", qualityCom, "--cookies", cookies, link);
                processBuilder.directory(new File(downloadDir));
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;

                while((line = bufferedReader.readLine()) != null)
                {
                    System.out.println(line);
                }

                int exitCode = process.waitFor();

                if (exitCode != 0)
                {
                    System.out.println("error: " + exitCode);
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

                for(File f : files)
                {
                    if (!f.equals(latest)) f.delete();
                }

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






