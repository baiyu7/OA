package cn.gson.oasys.controller.user;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import cn.gson.oasys.mappers.DeptMapper;
import cn.gson.oasys.mappers.UserMapper;
import cn.gson.oasys.model.dao.roledao.RoleDao;
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
    @Autowired
    RoleDao roleDao;

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
        List<Position> positions = pdao.findByDeptidAndNameNotLike(1L, "");
        System.out.println(deptmanage);
        List<User> formaluser = new ArrayList<>();
        List<User> deptusers = udao.findByDept(dept);

        for (User deptuser : deptusers) {
            if (deptuser.getIsLock() != 1) {
                if (deptuser.getFatherId() != -1) {
                    Position position = deptuser.getPosition();
                    System.out.println(deptuser.getRealName() + ":" + position.getName());

                        formaluser.add(deptuser);

                }

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
        //只允许普通员工调换
        User u = udao.findOne(userid);
        Role role = u.getRole();
        Dept deptold = deptdao.findOne(deptid);//老部门
        Dept deptnew = deptdao.findOne(changedeptid);//新部门
        //总经理不支持调换
        if (deptold.getDeptId() == deptnew.getDeptId()) {
            model.addAttribute("error", "自己往自己部门调用有意思？");
            return "user/deptprocess";
        }

        if (u.getRole().getRoleId() == 1) {
            model.addAttribute("error", "管理员不支持调换");
            return "user/deptprocess";
        }
        if (u.getFatherId() == -1) {//部门经理
            model.addAttribute("error", "部门经理不支持调换");
            return "user/deptprocess";
        } else if (u.getFatherId() == -2) {//总裁
            //当ceo
            if (positionid == 1) {
                model.addAttribute("error", "不支持调换管理员");
                return "user/deptprocess";
            } else if (positionid == 2) {//调换成CEo
                u.setFatherId((long) 0);
                Position position = pdao.findOne(positionid);

                Role role1 = roleDao.findOne((long) 2);
                u.setRole(role1);
                u.setPosition(position);
                udao.save(u);
                model.addAttribute("deptid", deptid);
                return "/readdept";
            } else if (positionid == 3) {//还是总裁
                udao.save(u);
                model.addAttribute("deptid", deptid);
                return "/readdept";
            } else {//总裁转换其他部门员工
                u.setFatherId(deptnew.getDeptmanager());
                u.setDept(deptnew);
                Position position = pdao.findOne(positionid);
                u.setPosition(position);
                Role role1 = roleDao.findOne((long) 5);
                u.setRole(role1);
                udao.save(u);
                model.addAttribute("deptid", deptid);
                return "/readdept";
            }

        } else {//CEo或者普通员工
            if (u.getRole().getRoleId() == 2) {//CEO
                if (positionid == 1) {//Ceo调换管理员
                    model.addAttribute("error", "不支持调换管理员");
                    return "user/deptprocess";
                } else if (positionid == 2) {//Ceo调换CEO
                    udao.save(u);
                    model.addAttribute("deptid", deptid);
                    return "/readdept";
                } else if (positionid == 3) {//Ceo调换总裁
                    u.setFatherId((long) -2);
                    u.setDept(deptnew);
                    Position position = pdao.findOne(positionid);
                    u.setPosition(position);
                    Role role1 = roleDao.findOne((long) 3);
                    u.setRole(role1);
                    udao.save(u);
                    model.addAttribute("deptid", deptid);
                    return "/readdept";
                } else {//ceo调换其他
                    u.setFatherId(deptnew.getDeptmanager());
                    u.setDept(deptnew);
                    Position position = pdao.findOne(positionid);
                    Role role1 = roleDao.findOne((long) 5);
                    u.setRole(role1);
                    u.setPosition(position);
                    udao.save(u);

                    model.addAttribute("deptid", deptid);
                    return "/readdept";
                }


            } else {//普通员工

                if (positionid == 1) {
                    model.addAttribute("error", "不支持调换成管理员");
                    return "user/deptprocess";
                } else if (positionid == 2) {
                    Position position = pdao.findOne(positionid);
                    u.setPosition(position);
                    Role role1 = roleDao.findOne((long) 2);
                    u.setRole(role1);
                    u.setDept(deptnew);
                    u.setFatherId((long) 0);
                    udao.save(u);
                    model.addAttribute("deptid", deptid);
                    return "/readdept";
                } else if (positionid == 3) {
                    u.setFatherId((long) -2);
                    Position position = pdao.findOne(positionid);
                    u.setPosition(position);
                    Role role1 = roleDao.findOne((long) 3);
                    u.setRole(role1);
                    u.setDept(deptnew);
                    udao.save(u);
                    model.addAttribute("deptid", deptid);
                    return "/readdept";
                } else {
                    u.setFatherId(deptnew.getDeptmanager());
                    u.setDept(deptnew);
                    Position position = pdao.findOne(positionid);
                    u.setPosition(position);
                    udao.save(u);
                    Role role1 = roleDao.findOne((long) 5);
                    u.setRole(role1);
                    model.addAttribute("deptid", deptid);
                    return "/readdept";
                }
            }
        }

    }


//        if (u.getUserId() == deptold.getDeptmanager()) {
//            model.addAttribute("error", "部门经理不支持调换");
//            return "user/deptprocess";
//        } else if (positionid == 1) {
//            model.addAttribute("error", "不支持调换成管理员");
//            return "user/deptprocess";
//        }else if(role.getRoleId() == 1){
//            model.addAttribute("error", "管理员不支持调换");
//            return "user/deptprocess";
//        }else{
//
//
//        }


      /*  if (role.getRoleId() == 2) {//CEO往出调
            //有部门经理就将fathorid设为部门经理的id
            u.setFatherId(deptnew.getDeptmanager());
            u.setDept(deptnew);
            Position position = pdao.findOne(positionid);
            u.setPosition(position);
            udao.save(u);

            model.addAttribute("deptid", deptid);
            return "/readdept";
        } else if (role.getRoleId() == 3) {//总裁往出调
            u.setFatherId(deptnew.getDeptmanager());
            u.setDept(deptnew);
            Position position = pdao.findOne(positionid);
            u.setPosition(position);
            udao.save(u);

            model.addAttribute("deptid", deptid);
            return "/readdept";
        } else if (role.getRoleId() == 4) {//部门经理往出调

        } else if () {

        } else {//员工
            //往总经办调换


        }

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

*//*
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
    public String deptmanagerchange(@RequestParam(value = "positionid", required = false) Long positionid,//转的职位
                                    @RequestParam(value = "changedeptid", required = false) Long changedeptid,//转的部门
                                    @RequestParam(value = "oldmanageid", required = false) Long oldmanageid,//转的人id
                                    @RequestParam(value = "newmanageid", required = false) Long newmanageid,//新领导的userid
                                    @RequestParam("deptid") Long deptid,//现在的部门id
                                    Model model) {

        //首先更换部门表的managerId
        //在将该部门人员的fathorid都换了
        System.out.println(positionid);
        if (positionid == null) {
            System.out.println("");
        }

        if (deptid == 1) {
            model.addAttribute("error", "该部门不支持设置部门经理");
            return "user/deptprocess";
        }
        if (positionid != null && positionid == 1) {
            model.addAttribute("error", "不支持设置管理员");
            return "user/deptprocess";
        }

        if (changedeptid == null) {//部门本身没领导
//            将自己设置为的信息设置为部门经理的，部门其他人员的fathorId设置成自己的userID
            //部门的managerId设置为自己的userId
            User user2 = udao.findOne(newmanageid);
            Dept deptnow = deptdao.findOne(deptid);

            user2.setFatherId((long) -1);
            Role role1 = roleDao.findOne((long) 4);
            user2.setRole(role1);
            udao.save(user2);
            deptnow.setDeptmanager(user2.getUserId());
            deptMapper.update(deptnow);

            List<User> list = udao.findByDept(deptnow);
            if (list.size() != 0) {
                for (User user : list) {
                    if (user.getUserId() != user2.getUserId()) {
                        User user1 = user;
                        user1.setFatherId(newmanageid);
                        userMapper.updateFatherId(user1);
                    }
                }
            }


            model.addAttribute("deptid", deptid);
            return "/readdept";

        } else {//有领导
            //如果部门是1的话就要分情况了下面有CEO、总裁
            User u = udao.findOne(oldmanageid);//要转的人
            Dept deptNew = deptdao.findOne(changedeptid);//转部门
            if (newmanageid == null) {//部门有一个人经理调换自己的话就要设置fathorId和roleId
                Position position = pdao.findOne(positionid);
                u.setPosition(position);
                Role role = roleDao.findOne((long) 5);
                u.setRole(role);
                u.setFatherId((long) 0);
                deptNew.setDeptmanager((long) 0);
                deptMapper.update(deptNew);
                udao.save(u);
                model.addAttribute("deptid", deptid);
                return "/readdept";
            }
            User user2 = udao.findOne(newmanageid);//新的部门领导
            Dept deptnow = deptdao.findOne(deptid);//老部门部门


            //     部门领导跟换的还是自己
            if (u.getUserId() == user2.getUserId()) {
                model.addAttribute("error", "自己换自己？");
                return "user/deptprocess";
            }

            //如果是自己内部转让
            if (deptnow.getDeptId() == deptNew.getDeptId()) {
                //先将老领导的fathorId和positionID还有Role设置了，
                u.setFatherId(user2.getUserId());
                Position position = pdao.findOne(positionid);
                u.setPosition(position);
                Role role = roleDao.findOne((long) 5);
                u.setRole(role);
                udao.save(u);

                //通过老领导的id搜索部门下属人员，将部门下属人员的fathor进行更改，更改部门的管理ID 最后设置新上任的领导
                List<User> list = udao.findByFatherId(oldmanageid);
                if (list.size() != 0) {
                    for (User user : list) {
                        if (user.getUserId() != user2.getUserId()) {
                            User user1 = user;
                            user1.setFatherId(newmanageid);
                            userMapper.updateFatherId(user1);
                        }
                    }
                }

                deptnow.setDeptmanager(user2.getUserId());
                deptMapper.update(deptnow);

                Role role1 = roleDao.findOne((long) 4);
                user2.setRole(role1);
                user2.setFatherId((long) -1);
                udao.save(user2);
                model.addAttribute("deptid", deptid);
                return "/readdept";

            }


            //     将要转的人的fathorId改了，roleId改了 部门改了就行了，新的经理就将fathorId改了，RoleId改了，子部门的人员的fathorId改了，部门manageId改了

            if (changedeptid == 1) {//这是往总裁办转人
                if (positionid == 2) {//如果是Ceo
                    Role role = roleDao.findOne((long) 2);
                    u.setRole(role);
                    u.setFatherId((long) 0);
                } else if (positionid == 3) {//如果是总裁
                    Role role = roleDao.findOne((long) 3);
                    u.setRole(role);
                    u.setFatherId((long) -2);
                }
                u.setDept(deptNew);
                Position position = pdao.findOne(positionid);
                u.setPosition(position);
                udao.save(u);


                List<User> list = udao.findByFatherId(oldmanageid);

                if (list.size() != 0) {


                    for (User user : list) {
                        if (user.getUserId() != user2.getUserId()) {
                            User user1 = user;
                            user1.setFatherId(newmanageid);
                            userMapper.updateFatherId(user1);
                        }

                    }
                }

                deptnow.setDeptmanager(user2.getUserId());
                deptMapper.update(deptnow);

                Role role = roleDao.findOne((long) 4);
                user2.setRole(role);
                user2.setFatherId((long) -1);
                udao.save(user2);
                model.addAttribute("deptid", deptid);
                return "/readdept";
            } else {//这是往别的部门添加人


//                查出要转部门的领导看有没有，有的话就将自己的fathorId设置为那个。Role设置为员工的部门和职位也要设置。
                u.setFatherId(deptNew.getDeptmanager());
                Role role = roleDao.findOne((long) 5);
                u.setRole(role);
                u.setDept(deptNew);
                Position position = pdao.findOne(positionid);
                u.setPosition(position);
                udao.save(u);


                //转进来的要设置部门的管理Id  自己的R}oleId FatherId为-1 该部门子员工的fatherId
                List<User> list = udao.findByFatherId(oldmanageid);
                if (list.size() != 0) {
                    for (User user : list) {
                        if (user.getUserId() != user2.getUserId()) {
                            User user1 = user;
                            user1.setFatherId(newmanageid);
                            userMapper.updateFatherId(user1);
                        }

                    }
                }

                deptnow.setDeptmanager(user2.getUserId());
                deptMapper.update(deptnow);
                Role role1 = roleDao.findOne((long) 4);
                user2.setRole(role1);

                user2.setFatherId((long) -1);
                udao.save(user2);
                model.addAttribute("deptid", deptid);
                return "/readdept";

            }
        }

//        System.out.println("oldmanageid:" + oldmanageid);//赵天麒id
//        System.out.println("newmanageid:" + newmanageid);//高圆圆id
//        Dept deptnow = deptdao.findOne(deptid);//老部门部门
//        Dept deptNew = deptdao.findOne(changedeptid);//新转部门
//
//
//        User user1 = udao.findOne(newmanageid);//高圆圆
//        User user = udao.findOne(oldmanageid);//查到的赵天麒
//       /* user1.setRole(user.getRole());

//
//        //将人员（赵天麒）进行换部门 他部门要有领导就要设置fatherid.，没有就设置null 还要改一下身份，role——id
//        if (deptNew.getDeptmanager() == null || deptNew.getDeptmanager() == 0) {//新部门没领导
//            user.setFatherId(null);
//            user.setDept(deptNew);
//            user.setRole(user1.getRole());
//            Position position = pdao.findOne(positionid);
//            user.setPosition(position);
//            udao.save(user);
//
//        } else {//有领导
//            user.setFatherId(deptnow.getDeptmanager());
//            user.setDept(deptNew);
//            Position position = pdao.findOne(positionid);
//            user.setPosition(position);
//            udao.save(user);
//        }
//
//
//        //将部门（高圆圆）的新领导的fathorid设置为0或者null ，将部门进行设置managerID设置为新领导的userId 、将部门下面的人员的fathorid设置为高圆圆的新领导的
//
//        user1.setFatherId((long) 0);
//
//        udao.save(user1);
//
//
//        deptnow.setDeptmanager(user1.getUserId());//将部门管理设置为高圆圆的 保存一下
//        deptMapper.update(deptnow);
//
//        List<User> list = udao.findByDept(deptnow);
//        //循环员工将他们的fatoorid设置为高圆圆的
//        for (int i = 0; i < list.size(); i++) {//把自己排除掉
//            User user2 = list.get(i);
//
//            if (user2.getUserId() != user1.getUserId()) {
//                user2.setFatherId(user1.getUserId());
//                userMapper.updateFatherId(user2);
//            }
//
//        }
//
//
//
//
//       *//* if (oldmanageid != null) {
//        User oldmanage = udao.findOne(oldmanageid);
//
//        Position namage = oldmanage.getPosition();
//
//        Dept changedept = deptdao.findOne(changedeptid);
//        Position changeposition = pdao.findOne(positionid);
//
//        oldmanage.setDept(changedept);
//        oldmanage.setPosition(changeposition);
//        udao.save(oldmanage);
//
//        if (newmanageid != null) {
//            User newmanage = udao.findOne(newmanageid);
//            newmanage.setPosition(namage);
//            deptnow.setDeptmanager(newmanageid);
//            deptdao.save(deptnow);
//            udao.save(newmanage);
//        } else {
//            deptnow.setDeptmanager(null);
//            deptdao.save(deptnow);
//        }
//
//    } else
//
//    {
//        User newmanage = udao.findOne(newmanageid);
//        Position manage = pdao.findByDeptidAndNameLike(deptid, "%经理").get(0);
//        newmanage.setPosition(manage);
//        deptnow.setDeptmanager(newmanageid);
//        deptdao.save(deptnow);
//        udao.save(newmanage);
//    }*/


    }
}
