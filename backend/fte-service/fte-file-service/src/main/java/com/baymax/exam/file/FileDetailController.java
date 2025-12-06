package com.baymax.exam.file;

import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.web.annotation.Inner;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ：Baymax
 * @date ：Created in 2023/2/5 8:36
 * @description：
 * @modified By：
 * @version:
 */

@Slf4j
@RestController
@RequestMapping("/files")
public class FileDetailController {

    @Autowired
    private FileStorageService fileStorageService;//注入实列

    /**
     * 上传文件，成功返回文件 url
     */
    @Inner
    @PostMapping("/upload")
    public String upload(MultipartFile file) {
        FileInfo fileInfo = fileStorageService.of(file)
                .setPath("static/") // 修改为新的路径，符合nginx配置
                .setObjectId("0")   //关联对象id，为了方便管理，不需要可以不写
                .setObjectType("0") //关联对象类型，为了方便管理，不需要可以不写
                .putAttr("role","user") //保存一些属性，可以在切面、保存上传记录、自定义存储平台等地方获取使用，不需要可以不写
                .setOriginalFilename(file.getOriginalFilename()) // 保留原始文件名
                .upload();  //将文件上传到对应地方
        return fileInfo == null ? "上传失败！" : fileInfo.getUrl();
    }

    /**
     * 上传图片，成功返回文件信息
     * 图片处理使用的是 https://github.com/coobird/thumbnailator
     */
    @Inner
    @PostMapping("/upload-image")
    public Result uploadImage(@RequestPart("file") MultipartFile file,
                              @RequestParam(required = false) String path,
                              @RequestParam(required = false) String id,
                              @RequestParam(required = false) String type
                              ) {
         UploadPretreatment fileStorage = fileStorageService.of(file);
         if(path!=null){
             // 确保路径以static/开头
             if(path.startsWith("/")){
                 path = path.substring(1); // 去掉开头的斜杠
             }
             if(!path.startsWith("static/")){
                 fileStorage.setPath("static/" + path);
             } else {
             fileStorage.setPath(path);
             }
         } else {
             fileStorage.setPath("static/");  // 默认使用static路径
         }
         if(id!=null){
             fileStorage.setObjectId("0") ;  //关联对象id，为了方便管理，不需要可以不写
         }
         if(type!=null){
             fileStorage.setObjectType("0"); //关联对象类型，为了方便管理，不需要可以不写
         }
        // 保留原始文件名
        fileStorage.setOriginalFilename(file.getOriginalFilename());
        String url = fileStorage.upload().getUrl();
        return Result.success(url);
    }
    public void saveImage( MultipartFile file,String path,String id,String type){

    }
}
