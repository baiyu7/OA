package cn.gson.oasys.controller.user;

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
        System.out.println(positionid);
        System.out.println(roleid);
        Dept dept = ddao.findOne(deptid);
        Position position = pdao.findOne(positionid);
        Role role = rdao.findOne(roleid);
        if (user.getUserId() == null) {
            // 分四种情况 分别是 管理员、普通员工、CEo和总裁、部门经理
            if (roleid == 1) {//管理员直接保存什么都不管

                model.addAttribute("success", 1);
                return "/usermanage";
            } else if (roleid == 2) {//CEO 查找总裁，将总裁的fathorid设为自己的
                saveUser(user, dept, role, position, model);


                return "/usermanage";
            } else if (roleid == 3) {//总裁
                //总裁的添加也不做特殊保存他的fatherId设置为-2
                saveUser(user, dept, role, position, model);
                model.addAttribute("success", 1);
                return "/usermanage";
            } else if (roleid == 4) {//部门经理
                //看是否存在部门经理
                System.out.println(dept.getDeptmanager());
                Dept dept1 = ddao.findOne(deptid);
                System.out.println(dept1);

                if (dept.getDeptmanager() == null || dept.getDeptmanager() == 0) {
                    saveUser(user, dept, role, position, model);
                    User lead = udao.findid(user.getUserName());//设置下面的员工为自己的userid

                    dept.setDeptmanager(lead.getUserId());
                    deptMapper.update(dept);

                    List<User> list = udao.findByDept(dept);
                    for (User user1 : list) {
                        User user11 = user1;
                        if (user11.getUserId() != user.getUserId()) {
                            user11.setFatherId(lead.getUserId());
                            userMapper.updateFatherId(user11);
                        }

                    }
                    model.addAttribute("success", 1);
                    return "/usermanage";

                } else {
                    model.addAttribute("errormess", "该部门已经存在经理了");
                    return "/usermanage";
                }

            } else {//员工
                saveUser(user, dept, role, position, model);
                model.addAttribute("success", 1);
                return "/usermanage";


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


        //如果添加经理则要看部门有没有经理  没有就直接添加，有的话给提示。员工的话有的话就要经fatherId进行设置
      /*  if (roleid == 4) {
            if (dept.getDeptmanager() == null) {

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

    public void saveUser(User user, Dept dept, Role role, Position position, Model model) throws PinyinException {
        String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
        user.setPinyin(pinyin);
        user.setPassword("123456");
        user.setDept(dept);
        user.setRole(role);
        user.setPosition(position);
        //设置自己的部门经理
        if (role.getRoleId() == 1 || role.getRoleId() == 2) {

        } else if (role.getRoleId() == 4) {
            user.setFatherId((long) -1);
        } else if (role.getRoleId() == 3) {
            user.setFatherId((long) -2);
        } else {
            user.setFatherId(dept.getDeptmanager());
        }

        udao.save(user);


    }

    @RequestMapping("deleteuser")
    public String deleteuser(@RequestParam("userid") Long userid, Model model) {
        User user = udao.findOne(userid);
        user.setIsLock(1);
        //如果是部门经理就将其下面的员工的fatherId设空
        //可以根据部门id查询该部门的所有员工并且将fatherID设为空.并且将部门的管理id设置为空


        //管理员、Ceo、总裁、部门经理、员工
        if (user.getRole().getRoleId() == 1) {
            model.addAttribute("errormess", "权限不够");
            return "/usermanage";
        } else if (user.getRole().getRoleId() == 4) {//部门经理
            userMapper.updateIsLock(user);
            user.setFatherId((long) 0);
            userMapper.updateFatherId(user);
            //部门的管理id要设置为0；
            Dept dept = ddao.findOne(user.getDept().getDeptId());
            dept.setDeptmanager((long) 0);
            int a = deptMapper.update(dept);
            //修改自己下面的员工

            List<User> list = udao.findByFatherId(userid);
            for (User userdemo : list) {
                User user1 = userdemo;
                user1.setFatherId((long) 0);
                userMapper.updateFatherId(user1);
            }
            model.addAttribute("success", 1);
            return "/usermanage";

        } else {//员工
            userMapper.updateIsLock(user);
            user.setFatherId((long) 0);
            userMapper.updateFatherId(user);
            model.addAttribute("success", 1);
            return "/usermanage";
        }

/*    else if (user.getRole().getRoleId() == 2) {//CEO
            userMapper.updateIsLock(user);
            user.setFatherId((long) 0);
            userMapper.updateFatherId(user);
        } else if (user.getRole().getRoleId() == 3) {//总裁
            userMapper.updateIsLock(user);
            user.setFatherId((long) 0);
            userMapper.updateFatherId(user);
        }*/


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
