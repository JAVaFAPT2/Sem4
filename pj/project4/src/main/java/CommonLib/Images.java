import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class Images {

    private final RestTemplate restTemplate;

    public Images(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String url = "TERABOX_UPLOAD_URL"; // Đường dẫn API của Terabox

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer YOUR_ACCESS_TOKEN"); // Nếu cần Token

        // Tạo Multipart Request
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", file.getResource());

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        // Gửi POST request đến Terabox
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        // Xử lý phản hồi
        return response.getBody();
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("fileName") String fileName) {
        String url = "TERABOX_DOWNLOAD_URL"; // Đường dẫn API của Terabox

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer YOUR_ACCESS_TOKEN"); // Nếu cần Token

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Gửi GET request để tải file
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url + "?file=" + fileName,
                HttpMethod.GET,
                requestEntity,
                byte[].class);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(response.getBody());
    }
    public String deleteFile(@RequestParam("fileName") String fileName) {
        String url = "TERABOX_DELETE_URL"; // Đường dẫn API xóa file của Terabox

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer YOUR_ACCESS_TOKEN"); // Nếu cần Token

        // Tạo Request để xóa file
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Gửi DELETE request để xóa file trên Terabox
        ResponseEntity<String> response = restTemplate.exchange(
                url + "?file=" + fileName,
                HttpMethod.DELETE,
                requestEntity,
                String.class
        );

        // Xử lý phản hồi từ API
        return response.getBody();
    }
}

