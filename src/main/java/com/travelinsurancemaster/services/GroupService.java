package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.GroupNames;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.Group;
import com.travelinsurancemaster.model.dto.json.CategoryDTO;
import com.travelinsurancemaster.model.dto.json.GroupDTO;
import com.travelinsurancemaster.repository.GroupRepository;
import com.travelinsurancemaster.util.ServiceUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 11.08.15.
 */

@Service
@Transactional(readOnly = true)
public class GroupService {
    private static final Logger log = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CategoryService categoryService;

    private Map<PlanType, List<GroupDTO>> groupsByPlanTypeCache = new ConcurrentHashMap<>();

    public Group getGroupByName(String name) {
        return groupRepository.findByName(name);
    }

    public List<Group> getAll() {
        return groupRepository.findAll(ServiceUtils.sortByFieldAscIgnoreCase("id"));
    }

    public List<Group> getAllWithFilterCategoriesSortedByFilterOrder() {
        return getAll().stream().peek(group -> group.getCategoryList().removeIf(Category::isNotDisplayAsFilter))
                .peek(group -> group.getCategoryList().sort(Comparator.comparing(Category::getFilterOrder)))
                .collect(Collectors.toList());
    }

    public Map<PlanType, List<GroupDTO>> getGroupByPlanTypeMap() {
        if (groupsByPlanTypeCache.isEmpty()) {
            groupsByPlanTypeCache.put(PlanType.COMPREHENSIVE, new ArrayList<>());
            groupsByPlanTypeCache.put(PlanType.LIMITED, new ArrayList<>());
            List<Group> groups = new ArrayList<>(getAll());
            Optional<CategoryDTO> lookBackPeriod = Optional.empty();
            for (Group group : groups) {
                if (StringUtils.equals(group.getName(), GroupNames.Loss_or_Delay)) {
                    GroupDTO lossOrDelay = new GroupDTO(group);
                    lossOrDelay.getCategories().removeIf(categoryDTO -> Objects.equals(categoryDTO.getCode(), CategoryCodes.TRIP_INTERRUPTION_RETURN_AIR_ONLY));
                    groupsByPlanTypeCache.get(PlanType.COMPREHENSIVE).add(lossOrDelay);
                    groupsByPlanTypeCache.get(PlanType.LIMITED).add(new GroupDTO(group));
                } else if (StringUtils.equals(group.getName(), GroupNames.Cancellation)) {
                    lookBackPeriod = group.getCategoryList().stream()
                            .filter(category -> Objects.equals(category.getCode(), CategoryCodes.LOOK_BACK_PERIOD))
                            .map(CategoryDTO::new).findAny();
                    groupsByPlanTypeCache.get(PlanType.COMPREHENSIVE).add(new GroupDTO(group));
                } else if (StringUtils.equals(group.getName(), GroupNames.Medical)) {
                    GroupDTO medical = new GroupDTO(group);
                    lookBackPeriod.ifPresent(categoryDTO -> medical.getCategories().add(categoryDTO));
                    groupsByPlanTypeCache.get(PlanType.COMPREHENSIVE).add(new GroupDTO(group));
                    groupsByPlanTypeCache.get(PlanType.LIMITED).add(medical);
                } else {
                    groupsByPlanTypeCache.get(PlanType.COMPREHENSIVE).add(new GroupDTO(group));
                    groupsByPlanTypeCache.get(PlanType.LIMITED).add(new GroupDTO(group));
                }
            }
        }
        return groupsByPlanTypeCache;
    }

    public void invalidateCache(){
        groupsByPlanTypeCache.clear();
    }

    // Pre-Existent conditions and look-back period: Display in Cancellation group if trip cost > $0.
    public List<Group> getGroupsByPlanType(PlanType planType) {
        List<Group> groups = new ArrayList<>(getAll());
        if (planType.getId() == PlanType.COMPREHENSIVE.getId()) {
            Group lossOrDelayGroup = groups.stream().filter(group -> StringUtils.equals(group.getName(), GroupNames.Loss_or_Delay)).findFirst().orElse(null);
            Category tripInterruptionAirOnly = categoryService.getByCode(CategoryCodes.TRIP_INTERRUPTION_RETURN_AIR_ONLY);
            lossOrDelayGroup.getCategoryList().remove(tripInterruptionAirOnly);
            return groups;
        } else {
            Group cancellation = null;
            Group medical = null;
            for (Group group : groups) {
                if (Objects.equals(group.getName(), GroupNames.Medical)) {
                    medical = group;
                }
                if (Objects.equals(group.getName(), GroupNames.Cancellation)) {
                    cancellation = group;
                }
            }
            if (cancellation != null && medical != null) {
                Category lookBackPeriod = cancellation.getCategoryList().stream().filter(category -> Objects.equals(category.getCode(), CategoryCodes.LOOK_BACK_PERIOD)).findAny().orElseGet(null);
                groups.remove(cancellation);
                medical.getCategoryList().add(lookBackPeriod);
            }
            return groups;
        }
    }

    public static boolean isCancellationGroup(Category category) {
        return category.getGroup() != null && Objects.equals(category.getGroup().getName(), GroupNames.Cancellation);
    }

    public static boolean isCancellationGroup(Category category, PlanType planType) {
        if (planType.getId() == PlanType.COMPREHENSIVE.getId()) {
            return category.getGroup() != null && Objects.equals(category.getGroup().getName(), GroupNames.Cancellation);
        } else {
            return category.getGroup() != null && Objects.equals(category.getGroup().getName(), GroupNames.Cancellation)
                    && !Objects.equals(category.getCode(), CategoryCodes.PRE_EX_WAIVER) && !Objects.equals(category.getCode(), CategoryCodes.LOOK_BACK_PERIOD);
        }
    }
}
