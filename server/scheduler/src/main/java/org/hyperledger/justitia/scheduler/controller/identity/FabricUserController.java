package org.hyperledger.justitia.scheduler.controller.identity;

import org.hyperledger.justitia.common.face.service.identity.read.UserReader;
import org.hyperledger.justitia.common.face.service.identity.write.UserWriter;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.identity.beans.SetUserBean;
import org.hyperledger.justitia.scheduler.controller.identity.format.FormatData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/identity/user")
public class FabricUserController {
    private final UserReader userReader;
    private final UserWriter userWriter;

    @Autowired
    public FabricUserController(UserReader userReader, UserWriter userWriter) {
        this.userReader = userReader;
        this.userWriter = userWriter;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean setUser(SetUserBean form) {
        FabricUser fabricUserInfo = FormatData.formatUser(form);
        userWriter.setUser(fabricUserInfo);
        return new ResponseBean().success();
    }

    @GetMapping
    public ResponseBean<List<FabricUser>> getUsers() {
        List<FabricUser> fabricUsersInfo = userReader.selectUsersBase();
        return new ResponseBean<List<FabricUser>>().success(fabricUsersInfo);
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean updateUser(SetUserBean form, @PathVariable("userId") String userId) {
        form.setUserName(userId);
        FabricUser fabricUserInfo = FormatData.formatUser(form);
        userWriter.updateUserInfo(fabricUserInfo);
        return new ResponseBean().success();
    }

    @DeleteMapping("/{userId}")
    public ResponseBean delete(@PathVariable("userId") String userId) {
        userWriter.deleteUser(userId);
        return new ResponseBean().success();
    }
}
