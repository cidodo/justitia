package org.hyperledger.justitia.scheduler.controller.identity;

import org.hyperledger.justitia.common.bean.identity.FabricUser;
import org.hyperledger.justitia.common.face.service.identity.FabricUserService;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.identity.beans.SetUserBean;
import org.hyperledger.justitia.scheduler.controller.identity.format.FormatData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequestMapping("/identity/user")
public class FabricUserController {
    private final FabricUserService fabricUserService;

    @Autowired
    public FabricUserController(FabricUserService fabricUserService) {
        this.fabricUserService = fabricUserService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean setUser(SetUserBean form) {
        FabricUser fabricUserInfo = FormatData.formatUser(form);
        fabricUserService.setUser(fabricUserInfo);
        return new ResponseBean().success();
    }

    @GetMapping
    public ResponseBean<Collection<FabricUser>> getUsers() {
        Collection<FabricUser> fabricUsersInfo = fabricUserService.getUsers();
        return new ResponseBean<Collection<FabricUser>>().success(fabricUsersInfo);
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean updateUser(SetUserBean form, @PathVariable("userId") String userId) {
        form.setUserName(userId);
        FabricUser fabricUserInfo = FormatData.formatUser(form);
        fabricUserService.updateUserInfo(fabricUserInfo);
        return new ResponseBean().success();
    }

    @DeleteMapping("/{userId}")
    public ResponseBean delete(@PathVariable("userId") String userId) {
        fabricUserService.deleteUser(userId);
        return new ResponseBean().success();
    }
}
