package org.hyperledger.justitia.scheduler.controller.node;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/host")
public class HostMachineController {
//    @Autowired
//    private HostManageService hostService;
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public Response addHost(AddHostEntity form) throws IOException, FileServerException {
//        try {
//            hostService.addHost(form);
//        } catch (DuplicateKeyException e) {
//            return new Response().failure("主机名称" + form.getHostName() + "已存在。");
//        }
//        return new Response().success("主机" + form.getHostName() + "添加成功。");
//    }
//
//    @DeleteMapping("/{hostName}")
//    public Response delHost(@PathVariable("hostName") String hostName) throws FileServerException {
//        hostService.deleteHost(hostName);
//        return new Response().success("主机" + hostName + "已删除。");
//    }
//
//    @PostMapping(value = "/{hostName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public Response updateHost(UpdateHostEntity form, @PathVariable("hostName") String hostName) throws IOException, FileServerException {
//        hostService.updateHost(hostName, form);
//        return new Response().success("主机" + hostName + "配置修改成功。");
//    }
//
//    @GetMapping("/{hostName}")
//    public Response getHost(@PathVariable("hostName") String hostName) {
//        Host host = hostService.getHost(hostName);
//        return new Response().success(host);
//    }
//
//    @GetMapping
//    public Response getHosts() {
//        List<Host> hosts = hostService.getHosts();
//        return new Response().success(hosts);
//    }

}
