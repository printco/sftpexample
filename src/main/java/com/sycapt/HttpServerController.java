package com.sycapt;

import com.jcraft.jsch.*;
import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Properties;

@RestController
public class HttpServerController {

    @RequestMapping( value = "/fileupload"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public String fileUpload(@RequestParam Map<String,String> postdata){

        System.out.println("Post data:");
        System.out.println(postdata.toString());

        JSONObject result = new JSONObject();
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession("ftpuser", "localhost", 22);
            session.setPassword( "ftppassword" );
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            session.setTimeout( 60 * 1000 ); // 60 second

            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            String srcPath = "e:/temp/";

            String destpath = postdata.get("destpath");
            for( Map.Entry<String,String> pair : postdata.entrySet() ){
                String key = pair.getKey();
                String filename = pair.getValue();
                if( key.contains("filename") ){
                    try {
                        String from = srcPath + filename;
                        String to = destpath + filename;
                        System.out.println("Upload from " + from +" to " + to);
                        sftpChannel.put( from, to, ChannelSftp.OVERWRITE);
                        result.put(filename, "Upload success");
                    } catch ( SftpException e ){
                        result.put(filename, "Upload failed: " + e.getMessage());
                    }
                }
            }

            sftpChannel.disconnect();
            session.disconnect();

        } catch (JSchException e) {
            result.put("error", e.getMessage());
        }

        return result.toJSONString();
    }

    @RequestMapping( value = "/onetimefileupload"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public String onetimeFileUpload(@RequestParam Map<String,String> postdata){

        System.out.println("Post data:");
        System.out.println(postdata.toString());

        JSONObject result = new JSONObject();
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession("ftpuser", "localhost", 22);
            session.setPassword( "ftppassword" );
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            session.setTimeout( 60 * 1000 ); // 60 second

            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            String srcPath = "e:/temp/";

            String destpath = postdata.get("destpath");

            try {
                String from = srcPath + "*.txt";
                String to = destpath;
                System.out.println("Upload from " + from +" to " + to);
                sftpChannel.put( from, to, ChannelSftp.OVERWRITE);
                result.put("message", "Upload files success");
            } catch ( SftpException e ){
                result.put("message", "Upload failed: " + e.getMessage());
            }


            sftpChannel.disconnect();
            session.disconnect();

        } catch (JSchException e) {
            result.put("error", e.getMessage());
        }

        return result.toJSONString();
    }
}
