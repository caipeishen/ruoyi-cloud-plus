package org.dromara.system.api;

/**
 * 数据权限服务
 *
 * @author Lion Li
 */
public interface RemoteDataScopeService {

    /**
     * 获取角色自定义权限语句
     */
    String getRoleCustom(Long roleId);

    /**
     * 获取部门和下级权限语句
     */
    String getDeptAndChild(Long deptId);

    /**
     * 部门经理数据权限(上级部门以下数据权限)
     * @param deptId
     * @return
     */
    String getDeptManage(Long deptId);

    /**
     * 上级公司数据权限(上级如果非公司继续向上找)
     * @param deptId
     * @return
     */
    String getDeptCompany(Long deptId);

    /**
     * 集团部门数据权限
     */
    String getDeptGroup(Long deptId);
}
