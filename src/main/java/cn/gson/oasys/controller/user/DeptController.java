package cn.gson.oasys.controller.user;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import cn.gson.oasys.mappers.DeptMapper;
import cn.gson.oasys.mappers.UserMapper;
import cn.gson.oasys.model.entity.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.gson.oasys.model.dao.user.DeptDao;
import cn.gson.oasys.model.dao.user.PositionDao;
import cn.gson.oasys.model.dao.user.UserDao;
import cn.gson.oasys.model.entity.user.Dept;
import cn.gson.oasys.model.entity.user.Position;
import cn.gson.oasys.model.entity.user.User;

@Controller
@RequestMapping("/")
public class DeptController {

    @Autowired
    DeptDao deptdao;
    @Autowired
    UserDao udao;
    @Autowired
    PositionDao pdao;
    @Autowired
    UserMapper userMapper;
    @Autowired
    DeptMapper deptMapper;

    /**
     * 第一次进入部门管理页面
     *
     * @return
     */
    @RequestMapping("deptmanage")
    public String deptmanage(Model model) {
        List<Dept> depts = (List<Dept>) deptdao.findAll();
        System.out.println(depts);
        model.addAttribute("depts", depts);
        return "user/deptmanage";
    }

    @RequestMapping(value = "deptedit", method = RequestMethod.POST)
    public String adddept(@Valid Dept dept, @RequestParam("xg") String xg, BindingResult br, Model model) {
        System.out.println(br.hasErrors());
        System.out.println(br.getFieldError());
        if (!br.hasErrors()) {
            System.out.println("没有错误");
            Dept adddept = deptdao.save(dept);
            if ("add".equals(xg)) {
                System.out.println("新增拉");
                Position jinli = new Position();
                jinli.setDeptid(adddept.getDeptId());
                jinli.setName("初级人员");
                Position wenyuan = new Position();
                wenyuan.setDeptid(adddept.getDeptId());
                wenyuan.setName("职员");
                pdao.save(jinli);
                pdao.save(wenyuan);
            }
            if (adddept != null) {
                System.out.println("插入成功");
                model.addAttribute("success", 1);
                return "/deptmanage";
            }
        }
        System.out.println("有错误");
        model.addAttribute("errormess", "错误！~");
        return "user/deptedit";
    }

    @RequestMapping(value = "deptedit", method = RequestMethod.GET)
    public String changedept(@RequestParam(value = "dept", required = false) Long deptId, Model model) {
        if (deptId != null) {
            Dept dept = deptdao.findOne(deptId);
            model.addAttribute("dept", dept);
        }
        return "user/deptedit";
    }

    @RequestMapping("readdept")
    public String readdept(@RequestParam(value = "deptid") Long deptId, Model model) {

        Dept dept = deptdao.findOne(deptId);
        User deptmanage = null;
        if (dept.getDeptmanager() != null) {
            deptmanage = udao.findOne(dept.getDeptmanager());
            model.addAttribute("deptmanage", deptmanage);
        }
        List<Dept> depts = (List<Dept>) deptdao.findAll();
        List<Position> positions = pdao.findByDeptidAndNameNotLike(1L, "%经理");
        System.out.println(deptmanage);
        List<User> formaluser = new ArrayList<>();
        List<User> deptusers = udao.findByDept(dept);

        for (User deptuser : deptusers) {
            Position position = deptuser.getPosition();
            System.out.println(deptuser.getRealName() + ":" + position.getName());
            if (!position.getName().endsWith("经理")) {
                formaluser.add(deptuser);
            }
        }
        System.out.println(deptusers);
        model.addAttribute("positions", positions);
        model.addAttribute("depts", depts);
        model.addAttribute("deptuser", formaluser);

        model.addAttribute("dept", dept);
        model.addAttribute("isread", 1);

        return "user/deptread";

    }

    @RequestMapping("deptandpositionchange")
    public String deptandpositionchange(@RequestParam("positionid") Long positionid,
                                        @RequestParam("changedeptid") Long changedeptid,
                                        @RequestParam("userid") Long userid,//用户id
                                        @RequestParam("deptid") Long deptid,//老部门id
                                        Model model) {
        //将该人的fathorID设置为部门的id。

        //要根据部门获取部门经理的id；设置父级id；如过没有部门经理，提示   更改一下部门和职位

        Dept dept = deptdao.findOne(changedeptid);//要新转的新部门

        Dept deptOld = deptdao.findOne(deptid);//老的部门
        if (dept.getDeptmanager() == null || dept.getDeptmanager() == 0) {//代表没有领导
            model.addAttribute("error", "该部门没有经理");
            return "user/deptprocess";
        } else {
            User u = udao.findOne(userid);
            u.setFatherId(dept.getDeptmanager());
            u.setDept(dept);
            Position position = pdao.findOne(positionid);
            u.setPosition(position);
            udao.save(u);

            model.addAttribute("deptid", deptid);
            return "/readdept";
        }


//        Dept dept = deptdao.findOne(changedeptid);
//        List<User> Puser = udao.findByDept(dept);

/*
        Iterator<User> iterator = Puser.iterator();
        while (iterator.hasNext()) {
            User puser = iterator.next();
            if (puser.getRole().getRoleId() != 4) {
                iterator.remove();
            }
            //这里要使用Iterator的remove方法移除当前对象，如果使用List的remove方法，则同样会出现ConcurrentModificationException
        }

        if (Puser == null) {
            model.addAttribute("errormess", "该部门没有经理");
            return "/readdept";
        }


        User user = udao.findOne(userid);
        Dept changedept = deptdao.findOne(changedeptid);
        Position position = pdao.findOne(positionid);
        user.setDept(changedept);
        user.setPosition(position);
        user.setFatherId(Puser.get(0).getUserId());
        udao.save(user);
        System.out.println(deptid);

        model.addAttribute("deptid", deptid);
        return "/readdept";*/
    }

    @RequestMapping("deletdept") //部门下面有人删除应该给予提示呀
    public String deletdept(@RequestParam("deletedeptid") Long deletedeptid, Model model) {
        Dept dept = deptdao.findOne(deletedeptid);
        //看部门下面是否有人啊
        List<User> userList = udao.findByDept(dept);
        if (userList.size() != 0) {
            model.addAttribute("error", "该部门下还有员工");
            return "user/process";
        } else {
            List<Position> ps = pdao.findByDeptid(deletedeptid);//将下面的职位也都删除了
            for (Position position : ps) {
                System.out.println(position);
                pdao.delete(position);
            }
            deptdao.delete(dept);
            model.addAttribute("success", "");
            return "user/process";
        }


    }

    @RequestMapping("deptmanagerchange")
    public String deptmanagerchange(@RequestParam(value = "positionid", required = false) Long positionid,
                                    @RequestParam(value = "changedeptid", required = false) Long changedeptid,//新部门id 测试部
                                    @RequestParam(value = "oldmanageid", required = false) Long oldmanageid,//赵天麒
                                    @RequestParam(value = "newmanageid", required = false) Long newmanageid,//新领导的userid
                                    @RequestParam("deptid") Long deptid,
                                    Model model) {

        //首先更换部门表的managerId
        //在将该部门人员的fathorid都换了
        System.out.println("oldmanageid:" + oldmanageid);//赵天麒id
        System.out.println("newmanageid:" + newmanageid);//高圆圆id
        Dept deptnow = deptdao.findOne(deptid);//老部门部门
        Dept deptNew = deptdao.findOne(changedeptid);//新转部门


        User user1 = udao.findOne(newmanageid);//高圆圆
        User user = udao.findOne(oldmanageid);//查到的赵天麒
        user1.setRole(user.getRole());


        //将人员（赵天麒）进行换部门 他部门要有领导就要设置fatherid.，没有就设置null 还要改一下身份，role——id
        if (deptNew.getDeptmanager() == null || deptNew.getDeptmanager() == 0) {//新部门没领导
            user.setFatherId(null);
            user.setDept(deptNew);
            user.setRole(user1.getRole());
            Position position = pdao.findOne(positionid);
            user.setPosition(position);
            udao.save(user);

        } else {//有领导
            user.setFatherId(deptnow.getDeptmanager());
            user.setDept(deptNew);
            Position position = pdao.findOne(positionid);
            user.setPosition(position);
            udao.save(user);
        }


        //将部门（高圆圆）的新领导的fathorid设置为0或者null ，将部门进行设置managerID设置为新领导的userId 、将部门下面的人员的fathorid设置为高圆圆的新领导的

        user1.setFatherId((long) 0);

        udao.save(user1);


        deptnow.setDeptmanager(user1.getUserId());//将部门管理设置为高圆圆的 保存一下
        deptMapper.update(deptnow);

        List<User> list = udao.findByDept(deptnow);
        //循环员工将他们的fatoorid设置为高圆圆的
        for (int i = 0; i < list.size(); i++) {//把自己排除掉
            User user2 = list.get(i);

            if (user2.getUserId() != user1.getUserId()) {
                user2.setFatherId(user1.getUserId());
                userMapper.updateFatherId(user2);
            }

        }




       /* if (oldmanageid != null) {
            User oldmanage = udao.findOne(oldmanageid);

            Position namage = oldmanage.getPosition();

            Dept changedept = deptdao.findOne(changedeptid);
            Position changeposition = pdao.findOne(positionid);

            oldmanage.setDept(changedept);
            oldmanage.setPosition(changeposition);
            udao.save(oldmanage);

            if (newmanageid != null) {
                User newmanage = udao.findOne(newmanageid);
                newmanage.setPosition(namage);
                deptnow.setDeptmanager(newmanageid);
                deptdao.save(deptnow);
                udao.save(newmanage);
            } else {
                deptnow.setDeptmanager(null);
                deptdao.save(deptnow);
            }

        } else {
            User newmanage = udao.findOne(newmanageid);
            Position manage = pdao.findByDeptidAndNameLike(deptid, "%经理").get(0);
            newmanage.setPosition(manage);
            deptnow.setDeptmanager(newmanageid);
            deptdao.save(deptnow);
            udao.save(newmanage);
        }*/


        model.addAttribute("deptid", deptid);
        return "/readdept";
    }
}
