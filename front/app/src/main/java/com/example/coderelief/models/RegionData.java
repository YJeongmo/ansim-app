package com.example.coderelief.models;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 대한민국 시-군-구-읍면동 지역 데이터
 */
public class RegionData {
    
    public static class Region {
        public String name;
        public LatLng location;
        public boolean hasSubRegions; // 하위 읍/면/동이 있는지 여부
        
        public Region(String name, double latitude, double longitude) {
            this.name = name;
            this.location = new LatLng(latitude, longitude);
            this.hasSubRegions = false;
        }
        
        public Region(String name, double latitude, double longitude, boolean hasSubRegions) {
            this.name = name;
            this.location = new LatLng(latitude, longitude);
            this.hasSubRegions = hasSubRegions;
        }
    }
    
    /**
     * 시/도 목록 가져오기
     */
    public static String[] getCities() {
        return new String[] {
            "서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시",
            "대전광역시", "울산광역시", "세종특별자치시", "경기도", "강원특별자치도",
            "충청북도", "충청남도", "전북특별자치도", "전라남도", "경상북도", "경상남도", "제주특별자치도"
        };
    }
    
    /**
     * 각 시/도별 군/구 데이터 가져오기
     */
    public static Map<String, List<Region>> getDistrictsMap() {
        Map<String, List<Region>> map = new HashMap<>();
        
        // 서울특별시 (hasSubRegions = true로 설정)
        List<Region> seoul = new ArrayList<>();
        seoul.add(new Region("강남구", 37.5172, 127.0473, true));
        seoul.add(new Region("강동구", 37.5301, 127.1238, true));
        seoul.add(new Region("강북구", 37.6396, 127.0257, true));
        seoul.add(new Region("강서구", 37.5509, 126.8495, true));
        seoul.add(new Region("관악구", 37.4784, 126.9516, true));
        seoul.add(new Region("광진구", 37.5384, 127.0822, true));
        seoul.add(new Region("구로구", 37.4954, 126.8874, true));
        seoul.add(new Region("금천구", 37.4519, 126.8955, true));
        seoul.add(new Region("노원구", 37.6542, 127.0568, true));
        seoul.add(new Region("도봉구", 37.6688, 127.0471, true));
        seoul.add(new Region("동대문구", 37.5744, 127.0396, true));
        seoul.add(new Region("동작구", 37.5124, 126.9393, true));
        seoul.add(new Region("마포구", 37.5663, 126.9019, true));
        seoul.add(new Region("서대문구", 37.5791, 126.9368, true));
        seoul.add(new Region("서초구", 37.4837, 127.0324, true));
        seoul.add(new Region("성동구", 37.5634, 127.0371, true));
        seoul.add(new Region("성북구", 37.5894, 127.0167, true));
        seoul.add(new Region("송파구", 37.5145, 127.1059, true));
        seoul.add(new Region("양천구", 37.5169, 126.8664, true));
        seoul.add(new Region("영등포구", 37.5264, 126.8963, true));
        seoul.add(new Region("용산구", 37.5326, 126.9900, true));
        seoul.add(new Region("은평구", 37.6027, 126.9291, true));
        seoul.add(new Region("종로구", 37.5735, 126.9792, true));
        seoul.add(new Region("중구", 37.5641, 126.9979, true));
        seoul.add(new Region("중랑구", 37.6063, 127.0925, true));
        map.put("서울특별시", seoul);
        
        // 부산광역시
        List<Region> busan = new ArrayList<>();
        busan.add(new Region("강서구", 35.2122, 128.9800));
        busan.add(new Region("금정구", 35.2428, 129.0927));
        busan.add(new Region("남구", 35.1362, 129.0845));
        busan.add(new Region("동구", 35.1295, 129.0454));
        busan.add(new Region("동래구", 35.2047, 129.0837));
        busan.add(new Region("부산진구", 35.1630, 129.0533));
        busan.add(new Region("북구", 35.1975, 128.9895));
        busan.add(new Region("사상구", 35.1527, 128.9910));
        busan.add(new Region("사하구", 35.1042, 128.9747));
        busan.add(new Region("서구", 35.0979, 129.0258));
        busan.add(new Region("수영구", 35.1454, 129.1128));
        busan.add(new Region("연제구", 35.1761, 129.0819));
        busan.add(new Region("영도구", 35.0913, 129.0679));
        busan.add(new Region("중구", 35.1065, 129.0323));
        busan.add(new Region("해운대구", 35.1631, 129.1635));
        busan.add(new Region("기장군", 35.2446, 129.2218));
        map.put("부산광역시", busan);
        
        // 대구광역시
        List<Region> daegu = new ArrayList<>();
        daegu.add(new Region("남구", 35.8463, 128.5973));
        daegu.add(new Region("달서구", 35.8298, 128.5326));
        daegu.add(new Region("달성군", 35.7745, 128.4313));
        daegu.add(new Region("동구", 35.8869, 128.6350));
        daegu.add(new Region("북구", 35.8858, 128.5828));
        daegu.add(new Region("서구", 35.8715, 128.5591));
        daegu.add(new Region("수성구", 35.8581, 128.6302));
        daegu.add(new Region("중구", 35.8690, 128.6060));
        map.put("대구광역시", daegu);
        
        // 인천광역시
        List<Region> incheon = new ArrayList<>();
        incheon.add(new Region("계양구", 37.5379, 126.7378));
        incheon.add(new Region("남동구", 37.4475, 126.7313));
        incheon.add(new Region("동구", 37.4738, 126.6433));
        incheon.add(new Region("미추홀구", 37.4635, 126.6501));
        incheon.add(new Region("부평구", 37.5069, 126.7218));
        incheon.add(new Region("서구", 37.5454, 126.6759));
        incheon.add(new Region("연수구", 37.4106, 126.6784));
        incheon.add(new Region("중구", 37.4738, 126.6216));
        incheon.add(new Region("강화군", 37.7469, 126.4882));
        incheon.add(new Region("옹진군", 37.4463, 126.6368));
        map.put("인천광역시", incheon);
        
        // 광주광역시
        List<Region> gwangju = new ArrayList<>();
        gwangju.add(new Region("광산구", 35.1396, 126.7934));
        gwangju.add(new Region("남구", 35.1327, 126.9026));
        gwangju.add(new Region("동구", 35.1460, 126.9232));
        gwangju.add(new Region("북구", 35.1742, 126.9116));
        gwangju.add(new Region("서구", 35.1520, 126.8895));
        map.put("광주광역시", gwangju);
        
        // 대전광역시
        List<Region> daejeon = new ArrayList<>();
        daejeon.add(new Region("대덕구", 36.3467, 127.4148));
        daejeon.add(new Region("동구", 36.3109, 127.4548));
        daejeon.add(new Region("서구", 36.3552, 127.3838));
        daejeon.add(new Region("유성구", 36.3623, 127.3567));
        daejeon.add(new Region("중구", 36.3255, 127.4211));
        map.put("대전광역시", daejeon);
        
        // 울산광역시
        List<Region> ulsan = new ArrayList<>();
        ulsan.add(new Region("남구", 35.5440, 129.3300));
        ulsan.add(new Region("동구", 35.5049, 129.4163));
        ulsan.add(new Region("북구", 35.5820, 129.3614));
        ulsan.add(new Region("중구", 35.5690, 129.3327));
        ulsan.add(new Region("울주군", 35.5225, 129.1424));
        map.put("울산광역시", ulsan);
        
        // 세종특별자치시
        List<Region> sejong = new ArrayList<>();
        sejong.add(new Region("세종시 전체", 36.4800, 127.2890));
        map.put("세종특별자치시", sejong);
        
        // 경기도 (주요 시/군)
        List<Region> gyeonggi = new ArrayList<>();
        gyeonggi.add(new Region("수원시", 37.2636, 127.0286, true));
        gyeonggi.add(new Region("성남시", 37.4201, 127.1262, true));
        gyeonggi.add(new Region("고양시", 37.6584, 126.8320, true));
        gyeonggi.add(new Region("용인시", 37.2411, 127.1776));
        gyeonggi.add(new Region("부천시", 37.5034, 126.7660));
        gyeonggi.add(new Region("안산시", 37.3219, 126.8309));
        gyeonggi.add(new Region("안양시", 37.3943, 126.9568));
        gyeonggi.add(new Region("남양주시", 37.6361, 127.2168));
        gyeonggi.add(new Region("화성시", 37.1995, 126.8310));
        gyeonggi.add(new Region("평택시", 36.9921, 127.1129));
        gyeonggi.add(new Region("의정부시", 37.7381, 127.0338));
        gyeonggi.add(new Region("시흥시", 37.3799, 126.8028));
        gyeonggi.add(new Region("파주시", 37.7599, 126.7800));
        gyeonggi.add(new Region("김포시", 37.6152, 126.7159));
        gyeonggi.add(new Region("광명시", 37.4786, 126.8644));
        gyeonggi.add(new Region("광주시", 37.4294, 127.2550));
        gyeonggi.add(new Region("군포시", 37.3617, 126.9352));
        gyeonggi.add(new Region("이천시", 37.2720, 127.4350));
        gyeonggi.add(new Region("양주시", 37.7852, 127.0459));
        gyeonggi.add(new Region("오산시", 37.1498, 127.0773));
        gyeonggi.add(new Region("구리시", 37.5943, 127.1296));
        gyeonggi.add(new Region("안성시", 37.0078, 127.2797));
        gyeonggi.add(new Region("포천시", 37.8948, 127.2004));
        gyeonggi.add(new Region("의왕시", 37.3449, 126.9683));
        gyeonggi.add(new Region("하남시", 37.5390, 127.2065));
        gyeonggi.add(new Region("여주시", 37.2983, 127.6378));
        gyeonggi.add(new Region("양평군", 37.4910, 127.4874));
        gyeonggi.add(new Region("동두천시", 37.9034, 127.0604));
        gyeonggi.add(new Region("과천시", 37.4292, 126.9876));
        gyeonggi.add(new Region("가평군", 37.8315, 127.5095));
        gyeonggi.add(new Region("연천군", 38.0965, 127.0745));
        map.put("경기도", gyeonggi);
        
        // 강원특별자치도 (주요 시/군)
        List<Region> gangwon = new ArrayList<>();
        gangwon.add(new Region("춘천시", 37.8813, 127.7300));
        gangwon.add(new Region("원주시", 37.3422, 127.9202));
        gangwon.add(new Region("강릉시", 37.7519, 128.8761));
        gangwon.add(new Region("동해시", 37.5247, 129.1143));
        gangwon.add(new Region("태백시", 37.1640, 128.9856));
        gangwon.add(new Region("속초시", 38.2070, 128.5918));
        gangwon.add(new Region("삼척시", 37.4500, 129.1656));
        gangwon.add(new Region("홍천군", 37.6975, 127.8886));
        gangwon.add(new Region("횡성군", 37.4914, 127.9851));
        gangwon.add(new Region("영월군", 37.1836, 128.4614));
        gangwon.add(new Region("평창군", 37.3708, 128.3897));
        gangwon.add(new Region("정선군", 37.3805, 128.6609));
        gangwon.add(new Region("철원군", 38.1467, 127.3135));
        gangwon.add(new Region("화천군", 38.1063, 127.7083));
        gangwon.add(new Region("양구군", 38.1098, 127.9897));
        gangwon.add(new Region("인제군", 38.0695, 128.1707));
        gangwon.add(new Region("고성군", 38.3807, 128.4682));
        gangwon.add(new Region("양양군", 38.0754, 128.6190));
        map.put("강원특별자치도", gangwon);
        
        // 충청북도 (주요 시/군)
        List<Region> chungbuk = new ArrayList<>();
        chungbuk.add(new Region("청주시", 36.6424, 127.4890));
        chungbuk.add(new Region("충주시", 36.9910, 127.9259));
        chungbuk.add(new Region("제천시", 37.1326, 128.1909));
        chungbuk.add(new Region("보은군", 36.4894, 127.7294));
        chungbuk.add(new Region("옥천군", 36.3012, 127.5719));
        chungbuk.add(new Region("영동군", 36.1750, 127.7834));
        chungbuk.add(new Region("증평군", 36.7850, 127.5817));
        chungbuk.add(new Region("진천군", 36.8551, 127.4360));
        chungbuk.add(new Region("괴산군", 36.8158, 127.7878));
        chungbuk.add(new Region("음성군", 36.9402, 127.6927));
        chungbuk.add(new Region("단양군", 36.9846, 128.3658));
        map.put("충청북도", chungbuk);
        
        // 충청남도 (주요 시/군)
        List<Region> chungnam = new ArrayList<>();
        chungnam.add(new Region("천안시", 36.8151, 127.1139));
        chungnam.add(new Region("공주시", 36.4465, 127.1189));
        chungnam.add(new Region("보령시", 36.3334, 126.6128));
        chungnam.add(new Region("아산시", 36.7898, 127.0017));
        chungnam.add(new Region("서산시", 36.7849, 126.4503));
        chungnam.add(new Region("논산시", 36.1871, 127.0989));
        chungnam.add(new Region("계룡시", 36.2742, 127.2487));
        chungnam.add(new Region("당진시", 36.8931, 126.6471));
        chungnam.add(new Region("금산군", 36.1089, 127.4879));
        chungnam.add(new Region("부여군", 36.2756, 126.9100));
        chungnam.add(new Region("서천군", 36.0806, 126.6917));
        chungnam.add(new Region("청양군", 36.4591, 126.8024));
        chungnam.add(new Region("홍성군", 36.6012, 126.6650));
        chungnam.add(new Region("예산군", 36.6826, 126.8469));
        chungnam.add(new Region("태안군", 36.7456, 126.2981));
        map.put("충청남도", chungnam);
        
        // 전북특별자치도 (주요 시/군)
        List<Region> jeonbuk = new ArrayList<>();
        jeonbuk.add(new Region("전주시", 35.8242, 127.1480));
        jeonbuk.add(new Region("군산시", 35.9676, 126.7369));
        jeonbuk.add(new Region("익산시", 35.9483, 126.9576));
        jeonbuk.add(new Region("정읍시", 35.5699, 126.8560));
        jeonbuk.add(new Region("남원시", 35.4163, 127.3903));
        jeonbuk.add(new Region("김제시", 35.8034, 126.8808));
        jeonbuk.add(new Region("완주군", 35.9053, 127.1604));
        jeonbuk.add(new Region("진안군", 35.7919, 127.4249));
        jeonbuk.add(new Region("무주군", 36.0071, 127.6610));
        jeonbuk.add(new Region("장수군", 35.6478, 127.5211));
        jeonbuk.add(new Region("임실군", 35.6177, 127.2858));
        jeonbuk.add(new Region("순창군", 35.3741, 127.1376));
        jeonbuk.add(new Region("고창군", 35.4352, 126.7019));
        jeonbuk.add(new Region("부안군", 35.7318, 126.7330));
        map.put("전북특별자치도", jeonbuk);
        
        // 전라남도 (주요 시/군)
        List<Region> jeonnam = new ArrayList<>();
        jeonnam.add(new Region("목포시", 34.8118, 126.3922));
        jeonnam.add(new Region("여수시", 34.7604, 127.6622));
        jeonnam.add(new Region("순천시", 34.9506, 127.4872));
        jeonnam.add(new Region("나주시", 35.0160, 126.7107));
        jeonnam.add(new Region("광양시", 34.9406, 127.6956));
        jeonnam.add(new Region("담양군", 35.3209, 126.9882));
        jeonnam.add(new Region("곡성군", 35.2818, 127.2919));
        jeonnam.add(new Region("구례군", 35.2022, 127.4632));
        jeonnam.add(new Region("고흥군", 34.6111, 127.2753));
        jeonnam.add(new Region("보성군", 34.7714, 127.0800));
        jeonnam.add(new Region("화순군", 35.0643, 126.9863));
        jeonnam.add(new Region("장흥군", 34.6814, 126.9069));
        jeonnam.add(new Region("강진군", 34.6420, 126.7672));
        jeonnam.add(new Region("해남군", 34.5731, 126.5990));
        jeonnam.add(new Region("영암군", 34.8002, 126.6967));
        jeonnam.add(new Region("무안군", 34.9904, 126.4816));
        jeonnam.add(new Region("함평군", 35.0663, 126.5159));
        jeonnam.add(new Region("영광군", 35.2772, 126.5121));
        jeonnam.add(new Region("장성군", 35.3018, 126.7840));
        jeonnam.add(new Region("완도군", 34.3110, 126.7552));
        jeonnam.add(new Region("진도군", 34.4868, 126.2634));
        jeonnam.add(new Region("신안군", 34.8261, 126.1077));
        map.put("전라남도", jeonnam);
        
        // 경상북도 (주요 시/군)
        List<Region> gyeongbuk = new ArrayList<>();
        gyeongbuk.add(new Region("포항시", 36.0190, 129.3435));
        gyeongbuk.add(new Region("경주시", 35.8562, 129.2247));
        gyeongbuk.add(new Region("김천시", 36.1397, 128.1139));
        gyeongbuk.add(new Region("안동시", 36.5684, 128.7294));
        gyeongbuk.add(new Region("구미시", 36.1195, 128.3446));
        gyeongbuk.add(new Region("영주시", 36.8056, 128.6237));
        gyeongbuk.add(new Region("영천시", 35.9733, 128.9386));
        gyeongbuk.add(new Region("상주시", 36.4109, 128.1590));
        gyeongbuk.add(new Region("문경시", 36.5864, 128.1867));
        gyeongbuk.add(new Region("경산시", 35.8250, 128.7414));
        gyeongbuk.add(new Region("의성군", 36.3526, 128.6972));
        gyeongbuk.add(new Region("청송군", 36.4365, 129.0572));
        gyeongbuk.add(new Region("영양군", 36.6666, 129.1124));
        gyeongbuk.add(new Region("영덕군", 36.4150, 129.3655));
        gyeongbuk.add(new Region("청도군", 35.6477, 128.7363));
        gyeongbuk.add(new Region("고령군", 35.7273, 128.2627));
        gyeongbuk.add(new Region("성주군", 35.9189, 128.2826));
        gyeongbuk.add(new Region("칠곡군", 35.9945, 128.4012));
        gyeongbuk.add(new Region("예천군", 36.6573, 128.4518));
        gyeongbuk.add(new Region("봉화군", 36.8930, 128.7323));
        gyeongbuk.add(new Region("울진군", 36.9930, 129.4006));
        gyeongbuk.add(new Region("울릉군", 37.4844, 130.9058));
        map.put("경상북도", gyeongbuk);
        
        // 경상남도 (주요 시/군)
        List<Region> gyeongnam = new ArrayList<>();
        gyeongnam.add(new Region("창원시", 35.2280, 128.6811));
        gyeongnam.add(new Region("진주시", 35.1800, 128.1076));
        gyeongnam.add(new Region("통영시", 34.8544, 128.4332));
        gyeongnam.add(new Region("사천시", 35.0037, 128.0642));
        gyeongnam.add(new Region("김해시", 35.2286, 128.8894));
        gyeongnam.add(new Region("밀양시", 35.5034, 128.7467));
        gyeongnam.add(new Region("거제시", 34.8806, 128.6211));
        gyeongnam.add(new Region("양산시", 35.3350, 129.0374));
        gyeongnam.add(new Region("의령군", 35.3222, 128.2619));
        gyeongnam.add(new Region("함안군", 35.2724, 128.4065));
        gyeongnam.add(new Region("창녕군", 35.5446, 128.4925));
        gyeongnam.add(new Region("고성군", 34.9730, 128.3229));
        gyeongnam.add(new Region("남해군", 34.8376, 127.8924));
        gyeongnam.add(new Region("하동군", 35.0673, 127.7514));
        gyeongnam.add(new Region("산청군", 35.4152, 127.8734));
        gyeongnam.add(new Region("함양군", 35.5204, 127.7252));
        gyeongnam.add(new Region("거창군", 35.6869, 127.9095));
        gyeongnam.add(new Region("합천군", 35.5664, 128.1656));
        map.put("경상남도", gyeongnam);
        
        // 제주특별자치도
        List<Region> jeju = new ArrayList<>();
        jeju.add(new Region("제주시", 33.4996, 126.5312));
        jeju.add(new Region("서귀포시", 33.2541, 126.5600));
        map.put("제주특별자치도", jeju);
        
        return map;
    }
    
    /**
     * 각 군/구별 읍/면/동 데이터 가져오기
     * 키 형식: "시도명 군구명" (예: "서울특별시 강남구")
     */
    public static Map<String, List<Region>> getSubDistrictsMap() {
        Map<String, List<Region>> map = new HashMap<>();
        
        // ========== 서울특별시 ==========
        
        // 강남구
        List<Region> gangnamDongs = new ArrayList<>();
        gangnamDongs.add(new Region("역삼동", 37.5006, 127.0364));
        gangnamDongs.add(new Region("삼성동", 37.5140, 127.0632));
        gangnamDongs.add(new Region("청담동", 37.5242, 127.0473));
        gangnamDongs.add(new Region("대치동", 37.4947, 127.0630));
        gangnamDongs.add(new Region("논현동", 37.5103, 127.0286));
        gangnamDongs.add(new Region("압구정동", 37.5279, 127.0286));
        gangnamDongs.add(new Region("신사동", 37.5202, 127.0204));
        gangnamDongs.add(new Region("도곡동", 37.4893, 127.0517));
        gangnamDongs.add(new Region("개포동", 37.4861, 127.0528));
        gangnamDongs.add(new Region("일원동", 37.4843, 127.0830));
        map.put("서울특별시 강남구", gangnamDongs);
        
        // 강동구
        List<Region> gangdongDongs = new ArrayList<>();
        gangdongDongs.add(new Region("명일동", 37.5511, 127.1479));
        gangdongDongs.add(new Region("고덕동", 37.5547, 127.1542));
        gangdongDongs.add(new Region("상일동", 37.5493, 127.1742));
        gangdongDongs.add(new Region("길동", 37.5378, 127.1423));
        gangdongDongs.add(new Region("둔촌동", 37.5293, 127.1378));
        gangdongDongs.add(new Region("암사동", 37.5516, 127.1279));
        gangdongDongs.add(new Region("성내동", 37.5304, 127.1264));
        gangdongDongs.add(new Region("천호동", 37.5383, 127.1238));
        map.put("서울특별시 강동구", gangdongDongs);
        
        // 강북구
        List<Region> gangbukDongs = new ArrayList<>();
        gangbukDongs.add(new Region("미아동", 37.6270, 127.0256));
        gangbukDongs.add(new Region("번동", 37.6392, 127.0391));
        gangbukDongs.add(new Region("수유동", 37.6377, 127.0188));
        gangbukDongs.add(new Region("우이동", 37.6612, 127.0119));
        map.put("서울특별시 강북구", gangbukDongs);
        
        // 강서구
        List<Region> gangseoSeoulDongs = new ArrayList<>();
        gangseoSeoulDongs.add(new Region("염창동", 37.5484, 126.8743));
        gangseoSeoulDongs.add(new Region("등촌동", 37.5509, 126.8607));
        gangseoSeoulDongs.add(new Region("화곡동", 37.5413, 126.8403));
        gangseoSeoulDongs.add(new Region("가양동", 37.5613, 126.8549));
        gangseoSeoulDongs.add(new Region("마곡동", 37.5606, 126.8276));
        gangseoSeoulDongs.add(new Region("내발산동", 37.5574, 126.8218));
        gangseoSeoulDongs.add(new Region("외발산동", 37.5242, 126.8123));
        gangseoSeoulDongs.add(new Region("공항동", 37.5619, 126.8101));
        map.put("서울특별시 강서구", gangseoSeoulDongs);
        
        // 관악구
        List<Region> gwanakDongs = new ArrayList<>();
        gwanakDongs.add(new Region("봉천동", 37.4823, 126.9520));
        gwanakDongs.add(new Region("신림동", 37.4841, 126.9298));
        gwanakDongs.add(new Region("남현동", 37.4726, 126.9798));
        map.put("서울특별시 관악구", gwanakDongs);
        
        // 송파구
        List<Region> songpaDongs = new ArrayList<>();
        songpaDongs.add(new Region("잠실동", 37.5133, 127.0823));
        songpaDongs.add(new Region("신천동", 37.5144, 127.0952));
        songpaDongs.add(new Region("풍납동", 37.5306, 127.1133));
        songpaDongs.add(new Region("송파동", 37.5050, 127.1117));
        songpaDongs.add(new Region("석촌동", 37.5053, 127.1048));
        songpaDongs.add(new Region("삼전동", 37.5015, 127.0927));
        songpaDongs.add(new Region("가락동", 37.4975, 127.1186));
        songpaDongs.add(new Region("문정동", 37.4857, 127.1226));
        songpaDongs.add(new Region("장지동", 37.4776, 127.1314));
        songpaDongs.add(new Region("방이동", 37.5115, 127.1144));
        songpaDongs.add(new Region("오금동", 37.5018, 127.1287));
        map.put("서울특별시 송파구", songpaDongs);
        
        // 서초구
        List<Region> seochoDongs = new ArrayList<>();
        seochoDongs.add(new Region("서초동", 37.4838, 127.0184));
        seochoDongs.add(new Region("잠원동", 37.5153, 127.0119));
        seochoDongs.add(new Region("반포동", 37.5041, 126.9978));
        seochoDongs.add(new Region("방배동", 37.4812, 126.9958));
        seochoDongs.add(new Region("양재동", 37.4844, 127.0352));
        seochoDongs.add(new Region("내곡동", 37.4605, 127.0793));
        map.put("서울특별시 서초구", seochoDongs);
        
        // 마포구
        List<Region> mapoDongs = new ArrayList<>();
        mapoDongs.add(new Region("아현동", 37.5570, 126.9560));
        mapoDongs.add(new Region("공덕동", 37.5443, 126.9515));
        mapoDongs.add(new Region("도화동", 37.5464, 126.9399));
        mapoDongs.add(new Region("용강동", 37.5413, 126.9363));
        mapoDongs.add(new Region("대흥동", 37.5540, 126.9594));
        mapoDongs.add(new Region("염리동", 37.5589, 126.9541));
        mapoDongs.add(new Region("신수동", 37.5488, 126.9417));
        mapoDongs.add(new Region("서강동", 37.5512, 126.9356));
        mapoDongs.add(new Region("서교동", 37.5562, 126.9189));
        mapoDongs.add(new Region("합정동", 37.5496, 126.9139));
        mapoDongs.add(new Region("망원동", 37.5561, 126.9058));
        mapoDongs.add(new Region("연남동", 37.5633, 126.9261));
        mapoDongs.add(new Region("성산동", 37.5669, 126.9130));
        mapoDongs.add(new Region("상암동", 37.5790, 126.8900));
        map.put("서울특별시 마포구", mapoDongs);
        
        // 영등포구
        List<Region> yeongdeungpoDongs = new ArrayList<>();
        yeongdeungpoDongs.add(new Region("영등포동", 37.5184, 126.9064));
        yeongdeungpoDongs.add(new Region("여의도동", 37.5219, 126.9245));
        yeongdeungpoDongs.add(new Region("당산동", 37.5345, 126.8974));
        yeongdeungpoDongs.add(new Region("도림동", 37.5134, 126.8954));
        yeongdeungpoDongs.add(new Region("문래동", 37.5179, 126.8946));
        yeongdeungpoDongs.add(new Region("양평동", 37.5344, 126.8903));
        yeongdeungpoDongs.add(new Region("신길동", 37.5089, 126.9138));
        yeongdeungpoDongs.add(new Region("대림동", 37.4930, 126.8969));
        map.put("서울특별시 영등포구", yeongdeungpoDongs);
        
        // 종로구
        List<Region> jongroDongs = new ArrayList<>();
        jongroDongs.add(new Region("청운효자동", 37.5888, 126.9699));
        jongroDongs.add(new Region("사직동", 37.5755, 126.9676));
        jongroDongs.add(new Region("삼청동", 37.5857, 126.9834));
        jongroDongs.add(new Region("부암동", 37.5928, 126.9635));
        jongroDongs.add(new Region("평창동", 37.6098, 126.9711));
        jongroDongs.add(new Region("무악동", 37.5741, 126.9558));
        jongroDongs.add(new Region("교남동", 37.5707, 126.9739));
        jongroDongs.add(new Region("가회동", 37.5811, 126.9848));
        jongroDongs.add(new Region("종로1·2·3·4가동", 37.5702, 126.9847));
        jongroDongs.add(new Region("종로5·6가동", 37.5711, 127.0005));
        jongroDongs.add(new Region("이화동", 37.5762, 127.0046));
        jongroDongs.add(new Region("혜화동", 37.5882, 127.0023));
        jongroDongs.add(new Region("창신동", 37.5743, 127.0120));
        map.put("서울특별시 종로구", jongroDongs);
        
        // ========== 경기도 ==========
        
        // 성남시 (주요 동만 샘플)
        List<Region> seongnamDongs = new ArrayList<>();
        seongnamDongs.add(new Region("분당구 정자동", 37.3652, 127.1149));
        seongnamDongs.add(new Region("분당구 서현동", 37.3837, 127.1233));
        seongnamDongs.add(new Region("분당구 이매동", 37.3917, 127.1288));
        seongnamDongs.add(new Region("분당구 야탑동", 37.4119, 127.1277));
        seongnamDongs.add(new Region("분당구 판교동", 37.3952, 127.1113));
        seongnamDongs.add(new Region("수정구 신흥동", 37.4363, 127.1456));
        seongnamDongs.add(new Region("수정구 태평동", 37.4203, 127.1463));
        seongnamDongs.add(new Region("중원구 성남동", 37.4367, 127.1373));
        seongnamDongs.add(new Region("중원구 상대원동", 37.4279, 127.1386));
        map.put("경기도 성남시", seongnamDongs);
        
        // 수원시 (주요 동만 샘플)
        List<Region> suwonDongs = new ArrayList<>();
        suwonDongs.add(new Region("영통구 영통동", 37.2392, 127.0776));
        suwonDongs.add(new Region("영통구 매탄동", 37.2640, 127.0444));
        suwonDongs.add(new Region("팔달구 인계동", 37.2653, 127.0308));
        suwonDongs.add(new Region("팔달구 매교동", 37.2680, 127.0122));
        suwonDongs.add(new Region("장안구 조원동", 37.3007, 127.0090));
        suwonDongs.add(new Region("장안구 파장동", 37.3013, 127.0521));
        suwonDongs.add(new Region("권선구 세류동", 37.2686, 127.0011));
        suwonDongs.add(new Region("권선구 고색동", 37.2553, 126.9795));
        map.put("경기도 수원시", suwonDongs);
        
        // 고양시 (주요 동만 샘플)
        List<Region> goyangDongs = new ArrayList<>();
        goyangDongs.add(new Region("일산동구 백석동", 37.6534, 126.7861));
        goyangDongs.add(new Region("일산동구 정발산동", 37.6589, 126.7743));
        goyangDongs.add(new Region("일산동구 마두동", 37.6544, 126.7714));
        goyangDongs.add(new Region("일산서구 주엽동", 37.6735, 126.7621));
        goyangDongs.add(new Region("일산서구 탄현동", 37.6845, 126.7592));
        goyangDongs.add(new Region("덕양구 화정동", 37.6337, 126.8340));
        goyangDongs.add(new Region("덕양구 행신동", 37.6152, 126.8344));
        map.put("경기도 고양시", goyangDongs);
        
        return map;
    }
}


