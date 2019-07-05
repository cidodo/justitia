package org.hyperledger.justitia.scheduler.controller.node;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/node/couchdb")
public class CouchdbController {

//    @Autowired
//    private TokenManager tokenManager;
//    @Autowired
//    private CouchdbService service;
//
//    //------------------------------------------------ local data tlsCert -----------------------------------------------
//    @GetMapping
//    public Response getCouchdbs() {
//        List<CouchdbNode> couchdbs = service.getCouchdbs();
//        return new Response().success(couchdbs);
//    }
//
//    //---------------------------------------------------- node manage -------------------------------------------------
////    @PostMapping()
//    public Response createCouchdb(@RequestBody @Valid CreateCouchdbEntity body, HttpServletRequest request) {
//        String requester = tokenManager.getRequester(request).getUserId();
//
//        try {
//            service.createCouchdb(requester, null, body);
//            return new Response().success("Couchdb节点" + body.getCouchdbName() + "创建成功");
//        } catch (RuntimeException e) {
//            return new Response().failure(e.getMessage());
//        }
//    }
//
//
//    //    @CouchdbNodePermissionVerify
////    @DeleteMapping("/{couchdbName}")
//    public Response deleteCouchdb(@PathVariable("couchdbName") String couchdbName) {
//        service.deleteCouchdb(couchdbName);
//        return new Response().success("Couchdb节点" + couchdbName + "已删除");
//    }
//
//    //    @CouchdbNodePermissionVerify
////    @PutMapping("/{couchdbName}/{oper}")
//    public Response changeStatus(@PathVariable("couchdbName") String couchdbName, @PathVariable("oper") String oper) {
//        DockerService.ChangeContainerStatusResult result = service.changeContainerStatus(couchdbName, DockerService.ContainerOper.fromString(oper));
//        if (result.isSuccess()) {
//            return new Response().success();
//        } else {
//            return new Response().failure(result.getErrMsg(), result.getLog());
//        }
//    }
}
