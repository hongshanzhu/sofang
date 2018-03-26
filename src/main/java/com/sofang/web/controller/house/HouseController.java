package com.sofang.web.controller.house;

import com.sofang.base.*;
import com.sofang.service.AddressService;
import com.sofang.service.HouseService;
import com.sofang.web.dto.HouseDTO;
import com.sofang.web.dto.SubwayDTO;
import com.sofang.web.dto.SubwayStationDTO;
import com.sofang.web.dto.SupportAddressDTO;
import com.sofang.web.form.RentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by gegf
 */
@Controller
public class HouseController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private HouseService houseService;

    /**
     * 获取所有支持的城市
     * @return
     */
    @ResponseBody
    @GetMapping("address/support/cities")
    public ResponseEntity getSupportCities(){
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAllCities();
        if(result.getResultSize() == 0){
            return ResponseEntity.createByErrorCodeMessage(StatusCode.NOT_FOUND);
        }
        return ResponseEntity.createBySuccess(result.getResult());
    }

    /**
     * 获取指定城市下的区/县
     * @param cityEnName
     * @return
     */
    @ResponseBody
    @RequestMapping("address/support/regions")
    public ResponseEntity getSupportRegions(@RequestParam(name="city_name")String cityEnName){
        ServiceMultiResult<SupportAddressDTO> addressResult = addressService.findAllRegionByCityName(cityEnName);
        if (addressResult.getResult() == null || addressResult.getTotal() < 1) {
            return ResponseEntity.createByErrorCodeMessage(StatusCode.NOT_FOUND);
        }
        return ResponseEntity.createBySuccess(addressResult.getResult());
    }

    /**
     * 获取具体城市所支持的地铁线路
     * @param cityEnName
     * @return
     */
    @GetMapping("address/support/subway/line")
    @ResponseBody
    public ResponseEntity getSupportSubwayLine(@RequestParam(name = "city_name") String cityEnName) {
        List<SubwayDTO> subways = addressService.findAllSubwayByCity(cityEnName);
        return ResponseEntity.createBySuccess(subways);
    }

    /**
     * 获取对应地铁线路所支持的地铁站点
     * @param subwayId
     * @return
     */
    @GetMapping("address/support/subway/station")
    @ResponseBody
    public ResponseEntity getSupportSubwayStation(@RequestParam(name = "subway_id") Long subwayId) {
        List<SubwayStationDTO> stationDTOS = addressService.findAllStationBySubway(subwayId);
        if (stationDTOS.isEmpty()) {
            return ResponseEntity.createByErrorCodeMessage(StatusCode.NOT_FOUND);
        }

        return ResponseEntity.createBySuccess(stationDTOS);
    }

    @GetMapping("rent/house")
    public String rentHousePage(@ModelAttribute RentFilter rentFilter,
                                Model model, HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (rentFilter.getCityEnName() == null) {
            String cityEnNameInSession = (String) session.getAttribute("cityEnName");
            if (cityEnNameInSession == null) {
                redirectAttributes.addAttribute("msg", "must_chose_city");
                return "redirect:/index";
            } else {
                rentFilter.setCityEnName(cityEnNameInSession);
            }
        } else {
            session.setAttribute("cityEnName", rentFilter.getCityEnName());
        }

        ServiceResult<SupportAddressDTO> city = addressService.findCity(rentFilter.getCityEnName());
        if (!city.isSuccess()) {
            redirectAttributes.addAttribute("msg", "must_chose_city");
            return "redirect:/index";
        }
        model.addAttribute("currentCity", city.getResult());

        ServiceMultiResult<SupportAddressDTO> addressResult = addressService.findAllRegionsByCityName(rentFilter.getCityEnName());
        if (addressResult.getResult() == null || addressResult.getTotal() < 1) {
            redirectAttributes.addAttribute("msg", "must_chose_city");
            return "redirect:/index";
        }

        ServiceMultiResult<HouseDTO> serviceMultiResult = houseService.query(rentFilter);

        model.addAttribute("total", serviceMultiResult.getTotal());
        model.addAttribute("houses", serviceMultiResult.getResult());

        if (rentFilter.getRegionEnName() == null) {
            rentFilter.setRegionEnName("*");
        }

        model.addAttribute("searchBody", rentFilter);
        model.addAttribute("regions", addressResult.getResult());

        model.addAttribute("priceBlocks", RentValueBlock.PRICE_BLOCK);
        model.addAttribute("areaBlocks", RentValueBlock.AREA_BLOCK);

        model.addAttribute("currentPriceBlock", RentValueBlock.matchPrice(rentFilter.getPriceBlock()));
        model.addAttribute("currentAreaBlock", RentValueBlock.matchArea(rentFilter.getAreaBlock()));

        return "rent-list";
    }

}
