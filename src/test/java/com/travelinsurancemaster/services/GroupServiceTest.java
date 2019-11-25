package com.travelinsurancemaster.services;

import com.travelinsurancemaster.InsuranceMasterApp;
import com.travelinsurancemaster.model.GroupNames;
import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.Group;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

;
;

/**
 * @author Artur.Chernov
 */

@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InsuranceMasterApp.class)
public class GroupServiceTest {

    @Autowired
    private GroupService groupService;

    @Test
    public void getGroupByName() throws Exception {
        Assert.assertEquals(groupService.getGroupByName(GroupNames.Cancellation).getName(), GroupNames.Cancellation);
    }

    @Test
    public void getAll() throws Exception {
        Assert.assertEquals(groupService.getAll().size(), 7);
    }

    @Test
    public void getAllWithFilterCategoriesSortedByFilterOrder() throws Exception {
        List<Group> groups = groupService.getAllWithFilterCategoriesSortedByFilterOrder();
        Assert.assertEquals(groups.size(), 7);
        Assert.assertEquals(groups.stream().filter(group -> Objects.equals(group.getName(), GroupNames.Cancellation)).map(Group::getCategoryList).findFirst().get().size(), 10);
        Assert.assertTrue(groups.stream().allMatch(group -> {
            Object[] orders = group.getCategoryList().stream().map(Category::getFilterOrder).toArray();
            return ArrayUtils.isSorted(Arrays.copyOf(orders, (int) orders.length, Integer[].class));
        }));
    }

    @Test
    public void getGroupsByPlanType() throws Exception {
        // TODO: implement later
    }

}