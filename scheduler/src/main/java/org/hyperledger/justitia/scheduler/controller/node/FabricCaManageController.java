package org.hyperledger.justitia.scheduler.controller.node;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/identity")
public class FabricCaManageController {
//    private final static ConcurrentHashMap<String, CAConfigInfo> CA_CONFIG_MAP = new ConcurrentHashMap<>();
//
//    @Autowired
//    private TokenManager tokenManager;
//    @Autowired
//    private FabricCaManageService manager;
//
//    private class CAConfigInfo {
//        private CreateRootCaEntity config;
//        private String requester;
//        private Long expires;
//
//        CAConfigInfo(CreateRootCaEntity config, String requester) {
//            this.config = config;
//            this.requester = requester;
//            this.expires = System.currentTimeMillis() + 3600000;    //3600000毫秒表示一小时
//        }
//
//        public CreateRootCaEntity getConfig() {
//            return config;
//        }
//
//        public String getRequester() {
//            return requester;
//        }
//
//        Long getExpires() {
//            return expires;
//        }
//    }
//
//    /**
//     * 创建根CA服务
//     */
//    @PostMapping("/tlsCert/root")
//    public Response createRootServer(@RequestBody @Valid CreateRootCaEntity body, HttpServletRequest request) {
//
//        String userId = tokenManager.getRequester(request).getUserId();
//        if (body.isUploadCert()) {     //用户上传根CA证书
//            int random = (int) (Math.random() * 1000);
//            long time = System.currentTimeMillis();
//            String createId = "" + time + random;
//
//            synchronized (CA_CONFIG_MAP) {
//                for (Map.Entry<String, CAConfigInfo> caConfigEntry : CA_CONFIG_MAP.entrySet()) {
//                    CAConfigInfo caConfig = caConfigEntry.getValue();
//                    if (time > caConfig.getExpires()) {
//                        CA_CONFIG_MAP.remove(caConfigEntry.getTlsKey());
//                    }
//                }
//                CA_CONFIG_MAP.put(createId, new CAConfigInfo(body, userId));
//            }
//
//            Map<String, String> data = new HashMap<>();
//            data.put("createId", createId);
//            return new Response().success(data);
//        } else {
//            DockerService.ChangeContainerStatusResult result = manager.createCaServer(body, userId, true);
//            if (result.isSuccess()) {
//                return new Response().success("CA容器创建成功");
//            } else {
//                return new Response().failure(result.getErrMsg(), result.getLog());
//            }
//        }
//    }
//
//    /**
//     * 上传根CA服务证书
//     */
//    @PostMapping(value = "/tlsCert/root/create/{createId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public Response createRootServer(@PathVariable("createId") String createId, MultipartFile tlsCert, MultipartFile tlsKey, HttpServletRequest request) {
//
//        CreateRootCaEntity config = null;
//        String requester = null;
//        synchronized (CA_CONFIG_MAP) {
//            CAConfigInfo configInfo = CA_CONFIG_MAP.remove(createId);
//            if (configInfo != null) {
//                requester = tokenManager.getRequester(request).getUserId();
//                if (requester.equals(configInfo.getRequester())) {
//                    config = configInfo.getConfig();
//                }
//            }
//        }
//        if (config == null) {
//            return new Response().failure("不存在对应于文件上传ID(" + createId + ")的配置信息");
//        }
//
//        config.setCertFile(tlsCert);
//        config.setKeyFile(tlsKey);
//        DockerService.ChangeContainerStatusResult result = manager.createCaServer(config, requester, true);
//        if (result.isSuccess()) {
//            return new Response().success("CA容器创建成功");
//        } else {
//            return new Response().failure(result.getErrMsg(), result.getLog());
//        }
//    }
//
//    /**
//     * 创建中间CA服务
//     */
//    @PostMapping("/tlsCert/intermediate")
//    public Response createIntermediateServer(@RequestBody @Valid CreateIntermediateCaEntity body, HttpServletRequest request) {
//
//        String userId = tokenManager.getRequester(request).getUserId();
//        DockerService.ChangeContainerStatusResult result = manager.createCaServer(body, userId, false);
//        if (result.isSuccess()) {
//            return new Response().success("CA容器创建成功");
//        } else {
//            return new Response().failure(result.getErrMsg(), result.getLog());
//        }
//    }
//
//
//    /**
//     * 删除server
//     */
//    @CaServerPermissionVerify
//    @DeleteMapping("/tlsCert/{serverName}")
//    public Response delServer(@PathVariable("serverName") String serverName, @RequestParam("checked") Boolean checked) {
//        if (checked == null) {
//            return new Response().failure("参数checked值为null,应该是布尔类型");
//        }
//
//        String msg = manager.deleteCaServer(serverName, checked);
//
//        Map<String, Object> data = new HashMap<>();
//        if (msg == null || msg.isEmpty()) {
//            msg = serverName + "及其子服务已删除";
//            data.put("deleted", true);
//        } else {
//            data.put("deleted", false);
//        }
//        data.put("msg", msg);
//        return new Response().success(data);
//    }
//
//    /**
//     * 操作fabric identity server容器
//     */
//    @CaServerPermissionVerify
//    @PutMapping("/tlsCert/{serverName}/{oper}")
//    public Response changeServerStatus(@PathVariable("serverName") String serverName, @PathVariable("oper") String oper) {
//        DockerService.ChangeContainerStatusResult result = manager.changeContainerStatus(serverName, DockerService.ContainerOper.fromString(oper));
//        if (result.isSuccess()) {
//            return new Response().success("CA服务" + serverName + " " + oper + "完成");
//        } else {
//            return new Response().failure(result.getErrMsg(), result.getLog());
//        }
//    }
//
//
//    /**
//     * 获取指定fabric ca默认的配置文件
//     */
//    @GetMapping("/tlsCert/config")
//    public Response getDefaultConfig() {
//        SetCaServerEntity defaultConfig = manager.getDefaultConfig();
//        return new Response().success(defaultConfig);
//    }
//
//    /**
//     * 获取指定fabric identity server的配置文件
//     */
//    @GetMapping("/tlsCert/config/{serverName}")
//    public Response getConfig(@PathVariable("serverName") String serverName) {
//        Map config = manager.getServerConfig(serverName);
//        return new Response().success(config);
//    }
//
//    /**
//     * 更新fabric identity server配置文件
//     */
//    @CaServerPermissionVerify
//    @PutMapping(value = "/tlsCert/config/{serverName}")
//    public Response setConfig(@PathVariable("serverName") String serverName, @RequestBody SetCaServerEntity body) {
//        DockerService.ChangeContainerStatusResult result = manager.setServerConfig(serverName, body);
//        if (result.isSuccess()) {
//            return new Response().success("CA服务" + serverName + "配置已更新");
//        } else {
//            return new Response().failure(result.getErrMsg(), result.getLog());
//        }
//    }
//
//    /**
//     * 获取CA证书
//     */
//    @GetMapping(value = "/tlsCert/root/{serverName}")
//    public ResponseEntity<byte[]> getRootCert(@PathVariable("serverName") String serverName) throws DownloadFileException {
//        File rootCert = manager.getCaCert(serverName);
//        ResponseEntity<byte[]> responseEntity = DownloadHelper.getResponseEntity(rootCert);
//        rootCert.delete();
//        return responseEntity;
//    }
}
