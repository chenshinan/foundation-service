package io.choerodon.foundation.api.controller

import io.choerodon.foundation.IntegrationTestConfiguration
import io.choerodon.foundation.api.dto.AdjustOrderDTO
import io.choerodon.foundation.api.dto.PageFieldDTO
import io.choerodon.foundation.api.dto.PageFieldUpdateDTO
import io.choerodon.foundation.infra.enums.PageCode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * @author shinan.chen
 * @since 2019/4/12
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class ProjectPageFieldControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    Long organizationId = 1L

    @Shared
    List<PageFieldDTO> list

    @Shared
    Long projectId = 1L

    def url = '/v1/projects/{project_id}/page_field'

    def "listQuery"() {
        given: '准备'
        def testPageCode = pageCode
        when: '根据方案编码获取字段列表'
        def entity = restTemplate.exchange(url + "/list?pageCode=" + testPageCode + "&organizationId=" + organizationId, HttpMethod.GET, null, Map, projectId)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponseContent = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().get("content") != null && entity.getBody().get("content").size() > 0) {
                        actResponseContent = true
                        list = entity.getBody().get("content")
                    }
                }
            }
        }
        actRequest == expRequest
        actResponseContent == expResponseContent

        where: '测试用例：'
        pageCode                    || expRequest | expResponseContent
        'test'                      || true       | false
        PageCode.AGILE_ISSUE_CREATE || true       | true
    }

    def "adjustFieldOrder"() {
        given: '准备'
        def testPageCode = PageCode.AGILE_ISSUE_CREATE
        AdjustOrderDTO adjust = new AdjustOrderDTO()
        adjust.before = true
        adjust.currentFieldId = list[1].fieldId
        adjust.outsetFieldId = list[0].fieldId

        when: '调整顺序'
        HttpEntity<AdjustOrderDTO> httpEntity = new HttpEntity<>(adjust)
        def entity = restTemplate.exchange(url + "/adjust_order?pageCode=" + testPageCode + "&organizationId=" + organizationId, HttpMethod.POST, httpEntity, PageFieldDTO.class, projectId)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().getId() != null) {
                        actResponse = true
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        expRequest | expResponse
        true       | true
    }

    def "update"() {
        given: '准备工作'
        def testPageCode = PageCode.AGILE_ISSUE_CREATE
        PageFieldUpdateDTO updateDTO = new PageFieldUpdateDTO()
        updateDTO.objectVersionNumber = list[2].objectVersionNumber
        updateDTO.display = true

        when: '更新优先级类型'
        HttpEntity<PageFieldUpdateDTO> httpEntity = new HttpEntity<>(updateDTO)
        def entity = restTemplate.exchange(url + '/{field_id}?pageCode=' + testPageCode + "&organizationId=" + organizationId, HttpMethod.PUT, httpEntity, PageFieldDTO.class, projectId, list[2].fieldId)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().getId() != null) {
                        actResponse = true
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        expRequest | expResponse
        true       | true
    }
}