package io.choerodon.foundation.api.controller.v1;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.foundation.api.dto.ObjectSchemeDTO;
import io.choerodon.foundation.api.dto.ObjectSchemeSearchDTO;
import io.choerodon.foundation.api.service.ObjectSchemeService;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author shinan.chen
 * @since 2019/4/4
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/object_scheme")
public class ProjectObjectSchemeController extends BaseController {
    @Autowired
    private ObjectSchemeService objectSchemeService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "分页查询对象方案列表")
    @CustomPageRequest
    @PostMapping
    public ResponseEntity<Page<ObjectSchemeDTO>> pageQuery(@ApiIgnore
                                                           @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                           @ApiParam(value = "项目id", required = true)
                                                           @PathVariable("project_id") Long projectId,
                                                           @ApiParam(value = "组织id", required = true)
                                                           @RequestParam Long organizationId,
                                                           @ApiParam(value = "search dto", required = true)
                                                           @RequestBody(required = false) ObjectSchemeSearchDTO searchDTO) {
        return new ResponseEntity<>(objectSchemeService.pageQuery(organizationId, pageRequest, searchDTO), HttpStatus.OK);
    }
}
