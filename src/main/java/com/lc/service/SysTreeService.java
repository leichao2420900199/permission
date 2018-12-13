package com.lc.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.lc.dao.SysAclMapper;
import com.lc.dao.SysAclModuleMapper;
import com.lc.dao.SysDeptMapper;
import com.lc.dto.AclDTO;
import com.lc.dto.AclModuleLevelDTO;
import com.lc.dto.DeptLevelDTO;
import com.lc.model.SysAcl;
import com.lc.model.SysAclModule;
import com.lc.model.SysDept;
import com.lc.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper deptMapper;
    @Resource
    private SysAclModuleMapper aclModuleMapper;
    @Resource
    private SysAclMapper aclMapper;
    @Resource
    private SysCoreService coreService;

    public List<AclModuleLevelDTO> userAclTree(int userId){
        List<SysAcl> userAclList = coreService.findUserAclList(userId);
        List<AclDTO> aclDTOList = Lists.newArrayList();
        for (SysAcl acl:userAclList){
            AclDTO dto = AclDTO.adapt(acl);
            dto.setHasAcl(true);
            dto.setChecked(true);
            aclDTOList.add(dto);
        }
        return aclListToTree(aclDTOList);
    }


    public List<DeptLevelDTO> deptTree(){
        List<SysDept> deptList = deptMapper.selectAllDept();
        List<DeptLevelDTO> dtoList = Lists.newArrayList();
        for(SysDept dept:deptList){
            DeptLevelDTO dto = DeptLevelDTO.adapt(dept);
            dtoList.add(dto);
        }

        return dtoListToTree(dtoList);
    }

    public List<DeptLevelDTO> dtoListToTree(List<DeptLevelDTO> dtoList){
        if(CollectionUtils.isEmpty(dtoList)){
            return Lists.newArrayList();
        }
        //level为key
        Multimap<String,DeptLevelDTO> levelDTOMultimap = ArrayListMultimap.create();
        List<DeptLevelDTO> rootList = Lists.newArrayList();
        for(DeptLevelDTO dto:dtoList){
            levelDTOMultimap.put(dto.getLevel(),dto);
            if(LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }

        //按照seq从小到大对集合排序
        Collections.sort(rootList, new Comparator<DeptLevelDTO>() {
            @Override
            public int compare(DeptLevelDTO o1, DeptLevelDTO o2) {
                return o1.getSeq()-o2.getSeq();
            }
        });
        transformDeptTree(rootList,LevelUtil.ROOT,levelDTOMultimap);
        return rootList;

    }

    public void transformDeptTree(List<DeptLevelDTO> dtoList,String level,Multimap<String,DeptLevelDTO> levelDTOMultimap){
        for (int i=0;i<dtoList.size();i++){
            //获取每一层
            DeptLevelDTO dto = dtoList.get(i);
            //处理当前层级的数据
            String nextLevel = LevelUtil.calculateLevel(level,dto.getId());
            //处理下一层级
            List<DeptLevelDTO> deptLevelDTOS = (List<DeptLevelDTO>) levelDTOMultimap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(deptLevelDTOS)){
                //排序
                Collections.sort(deptLevelDTOS, new Comparator<DeptLevelDTO>() {
                    @Override
                    public int compare(DeptLevelDTO o1, DeptLevelDTO o2) {
                        return o1.getSeq()-o2.getSeq();
                    }
                });
                //设置下一层
                dto.setDeptList(deptLevelDTOS);
                //进入下一层处理
                transformDeptTree(deptLevelDTOS,nextLevel,levelDTOMultimap);
            }
        }
    }

    public List<AclModuleLevelDTO> aclModuleTree() {
        List<SysAclModule> aclModuleList = aclModuleMapper.selectAllAclModule();
        List<AclModuleLevelDTO> dtoList = Lists.newArrayList();
        for(SysAclModule acl:aclModuleList){
            AclModuleLevelDTO dto = AclModuleLevelDTO.adapt(acl);
            dtoList.add(dto);
        }

        return aclModuleDTOListToTree(dtoList);
    }

    public List<AclModuleLevelDTO> aclModuleDTOListToTree(List<AclModuleLevelDTO> dtoList){
        if(CollectionUtils.isEmpty(dtoList)){
            return Lists.newArrayList();
        }
        //level为key
        Multimap<String,AclModuleLevelDTO> levelDTOMultimap = ArrayListMultimap.create();
        List<AclModuleLevelDTO> rootList = Lists.newArrayList();
        for(AclModuleLevelDTO dto:dtoList){
            levelDTOMultimap.put(dto.getLevel(),dto);
            if(LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }

        //按照seq从小到大对集合排序
        Collections.sort(rootList, new Comparator<AclModuleLevelDTO>() {
            @Override
            public int compare(AclModuleLevelDTO o1, AclModuleLevelDTO o2) {
                return o1.getSeq()-o2.getSeq();
            }
        });
        transformAclModuleTree(rootList,LevelUtil.ROOT,levelDTOMultimap);
        return rootList;

    }

    public void transformAclModuleTree(List<AclModuleLevelDTO> dtoList,String level,Multimap<String,AclModuleLevelDTO> levelDTOMultimap){
        for (int i=0;i<dtoList.size();i++){
            //获取每一层
            AclModuleLevelDTO dto = dtoList.get(i);
            //处理当前层级的数据
            String nextLevel = LevelUtil.calculateLevel(level,dto.getId());
            //处理下一层级
            List<AclModuleLevelDTO> aclModuleLevelDTOS = (List<AclModuleLevelDTO>) levelDTOMultimap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(aclModuleLevelDTOS)){
                //排序
                Collections.sort(aclModuleLevelDTOS, new Comparator<AclModuleLevelDTO>() {
                    @Override
                    public int compare(AclModuleLevelDTO o1, AclModuleLevelDTO o2) {
                        return o1.getSeq()-o2.getSeq();
                    }
                });
                //设置下一层
                dto.setAclModuleList(aclModuleLevelDTOS);
                //进入下一层处理
                transformAclModuleTree(aclModuleLevelDTOS,nextLevel,levelDTOMultimap);
            }
        }
    }

    public List<AclModuleLevelDTO> roleTree(int roleId) {
        //当前用户已经被分配的权限点
        List<SysAcl> userAclList = coreService.findCurrentUserAclList();
        //当前角色分配过的权限点
        List<SysAcl> roleAclList = coreService.findCurrentUserRoleAclList(roleId);
        //当前所有的权限点
        List<SysAcl> allAclList = aclMapper.selectAllAcl();


        Set<Integer> userAclIdSet = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        Set<Integer> roleAclIdSet = roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        List<AclDTO> aclDTOList = Lists.newArrayList();
        for (SysAcl acl:allAclList){
            AclDTO dto = AclDTO.adapt(acl);
            if(userAclIdSet.contains(acl.getId())){
                dto.setHasAcl(true);
            }
            if(roleAclIdSet.contains(acl.getId())){
                dto.setChecked(true);
            }
            aclDTOList.add(dto);
        }
        return aclListToTree(aclDTOList);
    }

    public List<AclModuleLevelDTO> aclListToTree(List<AclDTO> aclDTOList){
        if(CollectionUtils.isEmpty(aclDTOList)){
            return Lists.newArrayList();
        }

        List<AclModuleLevelDTO> aclModuleLevelDTOS = aclModuleTree();

        Multimap<Integer,AclDTO>  moduleIdAclMap = ArrayListMultimap.create();

        for(AclDTO acl:aclDTOList){
            if(acl.getStatus()==1){
                moduleIdAclMap.put(acl.getAclModuleId(),acl);
            }
        }
        bindAclsWithOrder(aclModuleLevelDTOS,moduleIdAclMap);
        return aclModuleLevelDTOS;
    }

    public void bindAclsWithOrder(List<AclModuleLevelDTO> aclModuleLevelDTOS,Multimap<Integer,AclDTO>  moduleIdAclMap){
       if(CollectionUtils.isEmpty(aclModuleLevelDTOS)){
           return;
       }

       for(AclModuleLevelDTO dto:aclModuleLevelDTOS){
           List<AclDTO> aclDTOList = (List<AclDTO>) moduleIdAclMap.get(dto.getId());
           if(CollectionUtils.isNotEmpty(aclDTOList)){
               Collections.sort(aclDTOList, new Comparator<AclDTO>() {
                   @Override
                   public int compare(AclDTO o1, AclDTO o2) {
                       return o1.getSeq() - o2.getSeq();
                   }
               });
               dto.setAclList(aclDTOList);
           }
           bindAclsWithOrder(dto.getAclModuleList(),moduleIdAclMap);
       }
    }
}
