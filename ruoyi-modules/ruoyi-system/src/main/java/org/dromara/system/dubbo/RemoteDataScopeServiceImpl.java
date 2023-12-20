package org.dromara.system.dubbo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.mybatis.helper.DataBaseHelper;
import org.dromara.system.api.RemoteDataScopeService;
import org.dromara.system.domain.SysDept;
import org.dromara.system.domain.SysRoleDept;
import org.dromara.system.domain.vo.SysDeptVo;
import org.dromara.system.mapper.SysDeptMapper;
import org.dromara.system.mapper.SysRoleDeptMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.dromara.common.core.utils.StringUtils.SEPARATOR;

/**
 * 数据权限 实现
 * <p>
 * 注意: 此Service内不允许调用标注`数据权限`注解的方法
 * 例如: deptMapper.selectList 此 selectList 方法标注了`数据权限`注解 会出现循环解析的问题
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteDataScopeServiceImpl implements RemoteDataScopeService {

    private final SysRoleDeptMapper roleDeptMapper;
    private final SysDeptMapper deptMapper;

    @Override
    public String getRoleCustom(Long roleId) {
        List<SysRoleDept> list = roleDeptMapper.selectList(
            new LambdaQueryWrapper<SysRoleDept>()
                .select(SysRoleDept::getDeptId)
                .eq(SysRoleDept::getRoleId, roleId));
        if (CollUtil.isNotEmpty(list)) {
            return StreamUtils.join(list, rd -> Convert.toStr(rd.getDeptId()));
        }
        return null;
    }

    @Override
    public String getDeptAndChild(Long deptId) {
        List<SysDept> deptList = deptMapper.selectList(new LambdaQueryWrapper<SysDept>()
            .select(SysDept::getDeptId)
            .apply(DataBaseHelper.findInSet(deptId, "ancestors")));
        List<Long> ids = StreamUtils.toList(deptList, SysDept::getDeptId);
        ids.add(deptId);
        if (CollUtil.isNotEmpty(ids)) {
            return StreamUtils.join(ids, Convert::toStr);
        }
        return null;
    }

    @Override
    public String getDeptManage(Long deptId) {
        SysDeptVo sysDeptVo = deptMapper.selectVoById(deptId);
        if (sysDeptVo != null) {
            if (StrUtil.isNotBlank(sysDeptVo.getAncestors())) {
                String[] split = sysDeptVo.getAncestors().split(SEPARATOR);
                if (split.length > 0) {
                    SysDeptVo parentVo = deptMapper.selectVoById(split[split.length-1]);
                    if (parentVo != null) {
                        // 返回数据
                        List<Long> idList = new ArrayList<>();
                        // 当前节点
                        idList.add(parentVo.getDeptId());
                        // 子节点
                        List<SysDept> childDeptList = deptMapper.selectList(new LambdaQueryWrapper<SysDept>()
                            .select(SysDept::getDeptId)
                            .apply(DataBaseHelper.findInSet(parentVo.getDeptId(), "ancestors")));
                        if (CollUtil.isNotEmpty(childDeptList)) {
                            List<Long> childIdList = StreamUtils.toList(childDeptList, SysDept::getDeptId);
                            idList.addAll(childIdList);
                        }
                        return StreamUtils.join(idList, Convert::toStr);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getDeptCompany(Long deptId) {
        SysDeptVo sysDeptVo = deptMapper.selectVoById(deptId);
        if (sysDeptVo != null) {
            if (StrUtil.isNotBlank(sysDeptVo.getAncestors())) {
                String[] split = sysDeptVo.getAncestors().split(SEPARATOR);
                if (split.length > 0) {
                    // 根据祖籍节点倒叙查找
                    for (int i = split.length - 1; i >= 0; i--) {
                        SysDeptVo parentVo = deptMapper.selectVoById(split[i]);
                        // sys_detp.dept_type 部门类型（1公司 2部门 3岗位），找到公司再执行下方代码
                        if (parentVo == null || !"1".equals(parentVo.getDeptType())) {
                            continue;
                        }
                        // 返回数据
                        List<Long> idList = new ArrayList<>();
                        // 当前节点
                        idList.add(parentVo.getDeptId());
                        // 子节点
                        List<SysDept> childDeptList = deptMapper.selectList(new LambdaQueryWrapper<SysDept>()
                            .select(SysDept::getDeptId)
                            .apply(DataBaseHelper.findInSet(parentVo.getDeptId(), "ancestors")));
                        if (CollUtil.isNotEmpty(childDeptList)) {
                            List<Long> childIdList = StreamUtils.toList(childDeptList, SysDept::getDeptId);
                            idList.addAll(childIdList);
                        }
                        return StreamUtils.join(idList, Convert::toStr);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getDeptGroup(Long deptId) {
        SysDeptVo sysDeptVo = deptMapper.selectVoById(deptId);
        if (sysDeptVo != null) {
            if (StrUtil.isNotBlank(sysDeptVo.getAncestors())) {
                String[] split = sysDeptVo.getAncestors().split(SEPARATOR);
                if (split.length > 0) {
                    List<SysDeptVo> sysDeptVos = deptMapper.selectVoBatchIds(List.of(split));
                    if (!CollectionUtils.isEmpty(sysDeptVos)) {
                        // 顶级部门
                        List<SysDeptVo> collect = sysDeptVos.stream().filter(dept -> dept.getParentId() == null || dept.getParentId() == 0).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(collect)) {
                            SysDeptVo topDept = collect.get(0);
                            List<SysDept> childDeptList = deptMapper.selectList(new LambdaQueryWrapper<SysDept>()
                                .select(SysDept::getDeptId)
                                .apply(DataBaseHelper.findInSet(topDept.getDeptId(), "ancestors")));
                            List<Long> childIdList = StreamUtils.toList(childDeptList, SysDept::getDeptId);
                            childIdList.add(topDept.getDeptId());
                            return StreamUtils.join(childIdList, Convert::toStr);
                        }
                    }
                }
            }
        }
        return null;
    }

}
