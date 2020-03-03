package cn.gson.oasys.controller.user;

import java.util.List;

import org.apache.ibatis.annotations.Param;
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

@Controller
@RequestMapping("/")
public class UserController {

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

    @RequestMapping(value = "useredit", method = RequestMethod.POST)
    public String usereditpost(User user,
                               @RequestParam("deptid") Long deptid,
                               @RequestParam("positionid") Long positionid,
                               @RequestParam("roleid") Long roleid,
                               @RequestParam(value = "isbackpassword", required = false) boolean isbackpassword,
                               Model model) throws PinyinException {
        System.out.println(user);
        System.out.println(deptid);
        System.out.println(positionid);
        System.out.println(roleid);
        Dept dept = ddao.findOne(deptid);
        Position position = pdao.findOne(positionid);
        Role role = rdao.findOne(roleid);
        //先添加部门总经理，再添加部门经理，在添加员工
        List<User> list = udao.findByDept(dept);//根据部分查询出所有的用户
        //添加总经理时判断有没有存在总经理
        boolean flag = false;//判断有没有部门经理
        boolean temp = false;//判断部门经理是否重复
        long fatherId = 0;


        if (user.getUserId() == null) {
            if (roleid == 3) {
                List<User> list1 = udao.findrole((long) 2.0);
                System.out.println(list1);
                fatherId = list1.get(0).getUserId();

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getRole().getRoleId() == 3) {
                        flag = true;
                        fatherId = list.get(i).getUserId();
                        break;
                    }
                }
                if (flag) {
                    model.addAttribute("errormess", "该部门存在总经理了");
                    return "/usermanage";
                } else {
                    String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                    user.setPinyin(pinyin);
                    user.setPassword("123456");
                    user.setDept(dept);
                    user.setRole(role);
                    user.setPosition(position);
                    user.setFatherId(fatherId);
                    udao.save(user);


                    model.addAttribute("success", 1);
                    return "/usermanage";
                }


            } else if (roleid == 4) {

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getRole().getRoleId() == 4) {
                        temp = true;
                        break;
                    }
                    if (list.get(i).getRole().getRoleId() == 3) {
                        flag = true;
                        fatherId = list.get(i).getUserId();
                    }
                }
                if (temp) {
                    model.addAttribute("errormess", "该部门已经存在经理");
                    return "/usermanage";
                } else {
                    if (flag) {

                        String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                        user.setPinyin(pinyin);
                        user.setPassword("123456");
                        user.setDept(dept);
                        user.setRole(role);
                        user.setPosition(position);
                        user.setFatherId(fatherId);
                        udao.save(user);


                        model.addAttribute("success", 1);
                        return "/usermanage";
                    } else {
                        model.addAttribute("errormess", "该部门没有总经理");
                        return "/usermanage";
                    }
                }


            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getRole().getRoleId() == 4) {
                        flag = true;
                        fatherId = list.get(i).getUserId();
                        break;
                    }
                }

                if (flag) {

                    String pinyin = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                    user.setPinyin(pinyin);
                    user.setPassword("123456");
                    user.setDept(dept);
                    user.setRole(role);
                    user.setPosition(position);
                    user.setFatherId(fatherId);
                    udao.save(user);

                    model.addAttribute("success", 1);
                    return "/usermanage";
                } else {
                    model.addAttribute("errormess", "该部门没有部门经理");
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

    @RequestMapping("deleteuser")
    public String deleteuser(@RequestParam("userid") Long userid, Model model) {
        User user = udao.findOne(userid);

        user.setIsLock(1);

        udao.save(user);

        model.addAttribute("success", 1);
        return "/usermanage";

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
