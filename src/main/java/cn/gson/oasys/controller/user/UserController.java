package cn.gson.oasys.controller.user;

import java.util.ArrayList;
import java.util.List;

import cn.gson.oasys.mappers.DeptMapper;
import cn.gson.oasys.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.util.StringUtil;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import cn.gson.oasys.model.dao.roledao.RoleDao;
import cn.gson.oasys.model.dao.user.DeptDao;
import cn.gson.oasys.model.dao.user.PositionDao;
import cn.gson.oasys.model.dao.user.UserDao;
import cn.gson.oasys.model.entity.role.Role;
import cn.gson.oasys.model.entity.user.Dept;
import cn.gson.oasys.model.entity.user.Position;
import cn.gson.oasys.model.entity.user.User;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class UserController {


    @Autowired
    DeptMapper deptMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserDao udao;
    @Autowired
    DeptDao ddao;
    @Autowired
    PositionDao pdao;
    @Autowired
    RoleDao rdao;


    @RequestMapping("userlogmanage")
    public String userlogmanage() {
        return "user/userlogmanage";
    }

    @RequestMapping("usermanage")
    public String usermanage(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Sort sort = new Sort(new Order(Direction.ASC, "dept"));
        Pageable pa = new PageRequest(page, size, sort);
        Page<User> userspage = udao.findByIsLock(0, pa);
        List<User> users = userspage.getContent();
        model.addAttribute("users", users);
        model.addAttribute("page", userspage);
        model.addAttribute("url", "usermanagepaging");
        return "user/usermanage";
    }

    @RequestMapping("usermanagepaging")
    public String userPaging(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             @RequestParam(value = "usersearch", required = false) String usersearch
    ) {
        Sort sort = new Sort(new Order(Direction.ASC, "dept"));
        Pageable pa = new PageRequest(page, size, sort);
        Page<User> userspage = null;
        if (StringUtil.isEmpty(usersearch)) {
            userspage = udao.findByIsLock(0, pa);
        } else {
            System.out.println(usersearch);
            userspage = udao.findnamelike(usersearch, pa);
        }
        List<User> users = userspage.getContent();
        model.addAttribute("users", users);
        model.addAttribute("page", userspage);
        model.addAttribute("url", "usermanagepaging");

        return "user/usermanagepaging";
    }


    @RequestMapping(value = "useredit", method = RequestMethod.GET)
    public String usereditget(@RequestParam(value = "userid", required = false) Long userid, Model model) {
        if (userid != null) {
            User user = udao.findOne(userid);
            model.addAttribute("where", "xg");
            model.addAttribute("user", user);
        }

        List<Dept> depts = (List<Dept>) ddao.findAll();
        List<Position> positions = (List<Position>) pdao.findAll();
        List<Role> roles = (List<Role>) rdao.findAll();

        model.addAttribute("depts", depts);
        model.addAttribute("positions", positions);
        model.addAttribute("roles", roles);
        return "user/edituser";
    }


    private String edit(boolean isbackpassword, Dept dept, Position position, Role role, User user, Model model) throws PinyinException {
        if (user.getUserId() == null) {
            String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
            user.setPinyin(pinyin);
            user.setPassword("123456");
            user.setDept(dept);
            user.setRole(role);
            user.setPosition(position);

            udao.save(user);
            model.addAttribute("success", 1);
            return "/usermanage";
        } else {
            User user2 = udao.findOne(user.getUserId());
            user2.setUserTel(user.getUserTel());
            user2.setRealName(user.getRealName());
            user2.setEamil(user.getEamil());
            user2.setAddress(user.getAddress());
            user2.setUserEdu(user.getUserEdu());
            user2.setSchool(user.getSchool());
            user2.setIdCard(user.getIdCard());
            user2.setSex(user.getSex());
            user2.setBank(user.getBank());
            user2.setThemeSkin(user.getThemeSkin());
            user2.setSalary(user.getSalary());
//            user2.setFatherId(user.getFatherId());
            if (isbackpassword) {
                user2.setPassword("123456");
            }
            user2.setDept(dept);
            user2.setRole(role);
            user2.setPosition(position);
            udao.save(user2);
            model.addAttribute("success", 1);
            return "/usermanage";
        }
    }

    private String error(String mess, Model model) {
        model.addAttribute("errormess", mess);
        return "user/usermanage";
    }

    @RequestMapping(value = "useredit", method = RequestMethod.POST)
    public String usereditpost(User user,
                               @RequestParam("deptid") Long deptid,
                               @RequestParam("positionid") Long positionid,
                               @RequestParam("roleid") Long roleid,
                               @RequestParam(value = "isbackpassword", required = false) boolean isbackpassword,
                               Model model, HttpServletRequest r) throws PinyinException {
        System.out.println(user);
        System.out.println(deptid);
        String arr[] = r.getParameterValues("deptCheck");//选择的部门
        System.out.println(positionid);
        System.out.println(roleid);
        Dept dept = ddao.findOne(deptid);
        Position position = pdao.findOne(positionid);
        Role role = rdao.findOne(roleid);


        if (user.getUserId() == null) {//判断是添加还是修改

            if (role.getRoleId() == 1) {//管理员
                String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                user.setPinyin(pinyin);
                user.setPassword("123456");
                user.setDept(dept);
                user.setRole(role);
                user.setPosition(position);

                udao.save(user);
                model.addAttribute("success", 1);
                return "/usermanage";
            } else if (role.getRoleId() == 4) {//部门经理

                //判断有没有部门经理，有的话报错。
                Dept dept1 = ddao.findOne(deptid);
                if (dept1.getDeptmanager() != null || dept1.getDeptmanager() != 0) {
                    model.addAttribute("errormess", "该部门有部门经理了");
                    return "user/usermanage";
                } else {

                    //查看有没有该部门的总裁，有的话将fatherId设置为 没有的话设置为空，下下属人员设置fatherID为自己的fathorId；
                    if (dept1.getMaxId() != 0 || dept1.getMaxId() != null) {

                        user.setFatherId(dept1.getMaxId());
                        udao.save(user);
                        User user1 = udao.findid(user.getUserName());

                        dept1.setDeptmanager(user1.getUserId());
                        deptMapper.update(dept1);
                        List<User> list = udao.findByDept(dept1);//所属该部门下的员工除了自己

                        for (int i = 0; i < list.size(); i++) {
                            User user11 = list.get(i);
                            if (user11.getUserId() != user1.getUserId()) {
                                user11.setFatherId(user1.getUserId());
                                userMapper.updateFatherId(user11);
                            }
                        }
                        model.addAttribute("success", 1);
                        return "/usermanage";

                    } else {
                        udao.save(user);
                        User user1 = udao.findid(user.getUserName());
                        dept1.setDeptmanager(user1.getUserId());
                        deptMapper.update(dept1);

                        List<User> list = udao.findByDept(dept1);//所属该部门下的员工除了自己

                        for (int i = 0; i < list.size(); i++) {
                            User user11 = list.get(i);
                            if (user11.getUserId() != user1.getUserId()) {
                                user11.setFatherId(user1.getUserId());
                                userMapper.updateFatherId(user11);
                            }
                        }
                        model.addAttribute("success", 1);
                        return "/usermanage";

                    }
                }


            } else if (role.getRoleId() == 3) {//总裁
                //总经理根据选择的部门进行最大部门的选择，并且子部门的经理的fathorId进行添加 有总裁就要设置总裁.总裁就一个
                //查询总裁
                List<User> list = udao.findAll();
                User FirstUser = null;//CEo
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getRole().getRoleId() == 2) {
                        FirstUser = list.get(i);
                    }
                }

                if (arr == null) {//没选择管理部门
                    if (FirstUser == null) {
                        String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                        user.setPinyin(pinyin);
                        user.setPassword("123456");
                        user.setDept(dept);
                        user.setRole(role);
                        user.setPosition(position);

                        udao.save(user);
                        model.addAttribute("success", 1);
                        return "/usermanage";
                    }
                    //有Ceo
                    user.setFatherId(FirstUser.getUserId());
                    String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                    user.setPinyin(pinyin);
                    user.setPassword("123456");
                    user.setDept(dept);
                    user.setRole(role);
                    user.setPosition(position);

                    udao.save(user);
                    model.addAttribute("success", 1);
                    return "/usermanage";

                } else {//选择管理部门了
                    boolean flag = false;
                    List<Dept> listdept = new ArrayList();//查询选择的部门列表
                    List<User> listuser = new ArrayList<>();//选中人员的部门经理

                    //判断部门有没有总裁了
                    for (int i = 0; i < arr.length; i++) {
                        Dept dept1 = ddao.findOne(Long.parseLong(arr[i]));
                        if (dept1.getMaxId() != 0) {
                            flag = true;
                        }
                    }
                    if (flag == true) {
                        model.addAttribute("errormess", "选中的部门有总裁管制了");
                        return "/usermanage";
                    } else {
                        for (int i = 0; i < arr.length; i++) {
                            Dept dept1 = ddao.findOne(Long.parseLong(arr[i]));
                            listdept.add(dept1);
                            if (dept1.getDeptmanager() != null) {
                                User deptLead = udao.findOne(dept1.getDeptmanager());
                                listuser.add(deptLead);
                            }
                        }

                        if (FirstUser == null) {//如果没有CEO
                            String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                            user.setPinyin(pinyin);
                            user.setPassword("123456");
                            user.setDept(dept);
                            user.setRole(role);
                            user.setPosition(position);

                            udao.save(user);
                            //设置每个部门的maxId是总裁的；
                            User user1 = udao.findid(user.getUserName());
                            for (int i = 0; i < listdept.size(); i++) {
                                Dept dept2 = listdept.get(i);
                                dept2.setMaxId(user1.getUserId());
                                System.out.println(dept2);
                                int a = deptMapper.updateMaxId(dept2);
                                System.out.println(a);
                            }

                            //在查出来部门经理的userId将他们的fathorId设置为总裁的
                            for (int i = 0; i < listuser.size(); i++) {
                                User userLead = listuser.get(i);
                                userLead.setFatherId(user1.getUserId());
                                System.out.println(userLead);
                                userMapper.updateFatherId(userLead);
                            }

                            model.addAttribute("success", 1);
                            return "/usermanage";

                        } else {
                            user.setFatherId(FirstUser.getUserId());//设置自己fathorId的id；
                            String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                            user.setPinyin(pinyin);
                            user.setPassword("123456");
                            user.setDept(dept);
                            user.setRole(role);
                            user.setPosition(position);

                            udao.save(user);
                            //设置每个部门的maxId是总裁的；

                            User user1 = udao.findid(user.getUserName());

                            for (int i = 0; i < listdept.size(); i++) {
                                Dept dept2 = listdept.get(i);
                                dept2.setMaxId(user1.getUserId());
                                System.out.println(dept2);
                                int a = deptMapper.updateMaxId(dept2);
                                System.out.println(a);
                            }

                            //在查出来部门经理的userId将他们的fathorId设置为总裁的
                            for (int i = 0; i < listuser.size(); i++) {
                                User userLead = listuser.get(i);
                                userLead.setFatherId(user1.getUserId());
                                System.out.println(userLead);
                                userMapper.updateFatherId(userLead);
                            }
                            model.addAttribute("success", 1);
                            return "/usermanage";

                        }
                    }
                }

            } else if (role.getRoleId() == 2) {//ceo
                //ceo就要全部的总裁
                List<User> userList = udao.findrole(roleid);

                if (userList.size() != 0) {
                    model.addAttribute("errormess", "有CEO了");
                    return "/usermanage";
                } else {
                    String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                    user.setPinyin(pinyin);
                    user.setPassword("123456");
                    user.setDept(dept);
                    user.setRole(role);
                    user.setPosition(position);

                    udao.save(user);
                    User CEO = udao.findid(user.getUserName());
                    List<User> userList1 = udao.findrole(roleid + 1);

                    dept.setMaxId(CEO.getUserId());
                    deptMapper.updateMaxId(dept);

                    for (int i = 0; i < userList1.size(); i++) {
                        User user1 = userList1.get(i);
                        user1.setFatherId(CEO.getUserId());
                        userMapper.updateFatherId(user1);
                    }
                    model.addAttribute("success", 1);
                    return "/usermanage";
                }


            } else {//普通员工
                //看有没有部门经理就行。
                Dept dept1 = ddao.findOne(deptid);
                if (dept1.getDeptmanager() == null || dept1.getDeptmanager() == 0) {
                    String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                    user.setPinyin(pinyin);
                    user.setFatherId((long) 0);
                    user.setPassword("123456");
                    user.setDept(dept);
                    user.setRole(role);
                    user.setPosition(position);

                    udao.save(user);
                    model.addAttribute("success", 1);
                    return "/usermanage";
                } else {
                    user.setFatherId(dept1.getDeptmanager());
                    String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                    user.setPinyin(pinyin);
                    user.setPassword("123456");
                    user.setDept(dept);
                    user.setRole(role);
                    user.setPosition(position);

                    udao.save(user);
                    model.addAttribute("success", 1);
                    return "/usermanage";

                }
            }


        } else {
            User user2 = udao.findOne(user.getUserId());
            user2.setUserTel(user.getUserTel());
            user2.setRealName(user.getRealName());
            user2.setEamil(user.getEamil());
            user2.setAddress(user.getAddress());
            user2.setUserEdu(user.getUserEdu());
            user2.setSchool(user.getSchool());
            user2.setIdCard(user.getIdCard());
            user2.setSex(user.getSex());
            user2.setBank(user.getBank());
            user2.setThemeSkin(user.getThemeSkin());
            user2.setSalary(user.getSalary());
//            user2.setFatherId(user.getFatherId());
            if (isbackpassword) {
                user2.setPassword("123456");
            }
            user2.setDept(dept);
            user2.setRole(role);
            user2.setPosition(position);
            udao.save(user2);
            model.addAttribute("success", 1);
            return "/usermanage";
        }


        // 分四种情况 分别是 管理员、普通员工、经理和总裁


        //如果添加经理则要看部门有没有经理  没有就直接添加，有的话给提示。员工的话有的话就要经fatherId进行设置
      /*  if (roleid == 4) {
            if (dept.getDeptmanager() == null) {
                if (user.getUserId() == null) {
                    String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                    user.setPinyin(pinyin);
                    user.setPassword("123456");
                    user.setDept(dept);
                    user.setRole(role);
                    user.setPosition(position);
                    //总裁的userid
                    user.setFatherId(Long.parseLong(dept.getMaxId()));
                    udao.save(user);
                    model.addAttribute("success", 1);
                    return "/usermanage";
                } else {
                    User user2 = udao.findOne(user.getUserId());
                    user2.setUserTel(user.getUserTel());
                    user2.setRealName(user.getRealName());
                    user2.setEamil(user.getEamil());
                    user2.setAddress(user.getAddress());
                    user2.setUserEdu(user.getUserEdu());
                    user2.setSchool(user.getSchool());
                    user2.setIdCard(user.getIdCard());
                    user2.setSex(user.getSex());
                    user2.setBank(user.getBank());
                    user2.setThemeSkin(user.getThemeSkin());
                    user2.setSalary(user.getSalary());
//            user2.setFatherId(user.getFatherId());
                    if (isbackpassword) {
                        user2.setPassword("123456");
                    }
                    user2.setDept(dept);
                    user2.setRole(role);
                    user2.setPosition(position);
                    udao.save(user2);
                    model.addAttribute("success", 1);
                    return "/usermanage";
                }
            } else {
                model.addAttribute("errormess", "该部门已经存在经理了");
                return "/usermanage";
            }

        } else {//如果是普通员工
            if (user.getUserId() == null) {
                String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                user.setPinyin(pinyin);
                user.setPassword("123456");
                user.setDept(dept);
                user.setRole(role);
                user.setPosition(position);
                //设置自己的部门经理
                user.setFatherId(dept.getDeptmanager());
                udao.save(user);

                model.addAttribute("success", 1);
                return "/usermanage";


            } else {
                User user2 = udao.findOne(user.getUserId());
                user2.setUserTel(user.getUserTel());
                user2.setRealName(user.getRealName());
                user2.setEamil(user.getEamil());
                user2.setSex(user.getSex());
                user2.setAddress(user.getAddress());
                user2.setUserEdu(user.getUserEdu());
                user2.setSchool(user.getSchool());
                user2.setIdCard(user.getIdCard());
                user2.setBank(user.getBank());
                user2.setThemeSkin(user.getThemeSkin());
                user2.setSalary(user.getSalary());
                if (isbackpassword) {
                    user2.setPassword("123456");
                }
                user2.setDept(dept);
                user2.setRole(role);
                user2.setPosition(position);
                udao.save(user2);
                model.addAttribute("success", 1);
                return "/usermanage";
            }
        }*/

    }


    @RequestMapping("deleteuser")
    public String deleteuser(@RequestParam("userid") Long userid, Model model) {
        User user = udao.findOne(userid);
        user.setIsLock(1);
        //如果是部门经理就将其下面的员工的fatherId设空
        //可以根据部门id查询该部门的所有员工并且将fatherID设为空.并且将部门的管理id设置为空
        if (user.getRole().getRoleId() == 4) {

            Dept dept = ddao.findOne(user.getDept().getDeptId());
            dept.setDeptmanager((long) 0);
            int a = deptMapper.update(dept);


            List<User> list = udao.findByFatherId(userid);


            for (User userdemo : list) {
                User user1 = userdemo;
                user1.setFatherId((long) 0);
                userMapper.updateFatherId(user1);
            }

            userMapper.updateIsLock(user);
            model.addAttribute("success", 1);
            return "/usermanage";
        } else {
            udao.save(user);

            model.addAttribute("success", 1);
            return "/usermanage";
        }


    }


    @RequestMapping("useronlyname")
    public @ResponseBody
    boolean useronlyname(@RequestParam("username") String username) {
        System.out.println(username);
        User user = udao.findByUserName(username);
        System.out.println(user);
        if (user == null) {
            return true;
        }
        return false;
    }

    @RequestMapping("selectdept")
    public @ResponseBody
    List<Position> selectdept(@RequestParam("selectdeptid") Long deptid) {

        return pdao.findByDeptidAndNameNotLike(deptid, "%经理");
    }


}
