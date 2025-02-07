package common.externalApi;

public class GpsTransfer {

    private double lat; //gps로 반환받은 위도
    private double lon; //gps로 반환받은 경도

    private double xLat; //x좌표로 변환된 위도
    private double yLon; //y좌표로 변환된 경도

    public GpsTransfer() {}

    public GpsTransfer(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getxLat() {
        return xLat;
    }

    public double getyLon() {
        return yLon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setxLat(double xLat) {
        this.xLat = xLat;
    }

    public void setyLon(double yLon) {
        this.yLon = yLon;
    }

    //x,y좌표로 변환해주는것
    public void transfer(GpsTransfer gpt, int mode){

        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //


        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        if (mode == 0) {
//            rs.lat = lat_X; //gps 좌표 위도
//            rs.lng = lng_Y; //gps 좌표 경도
            double ra = Math.tan(Math.PI * 0.25 + (gpt.getLat()) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = gpt.getLon() * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            double x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            double y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
            gpt.setxLat(x);
            gpt.setyLon(y);
//            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
//            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        }
        else {
//            rs.x = lat_X; //기존의 x좌표
//            rs.y = lng_Y; //기존의 경도
            double xlat = gpt.getxLat();
            double ylon = gpt.getyLon();
            double xn = xlat - XO;
            double yn = ro - ylon + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            double theta = 0.0;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            }
            else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                }
                else theta = Math.atan2(xn, yn);
            }
            double alon = theta / sn + olon;
//            rs.lat = alat * RADDEG; //gps 좌표 위도
//            rs.lng = alon * RADDEG; //gps 좌표 경도
            gpt.setLat(alat * RADDEG);
            gpt.setLon(alon * RADDEG);
        }
    }

    @Override
    public String toString() {
        return "GpsTransfer{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", xLat=" + xLat +
                ", yLon=" + yLon +
                '}';
    }
}
