package top.lzhseu.utils;

import java.net.UnknownHostException;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetAddress;
import java.util.Collections;
import java.net.NetworkInterface;
import java.util.Enumeration;


/**
 * @author lzh
 * @date 2020/12/8 11:13
 */
@Slf4j
public class NetUtil {

    public static ArrayList<String> getLocalIpAddr() {

        ArrayList<String> ipList = new ArrayList<>();

        try {

            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();

            while(interfaces.hasMoreElements()) {

                NetworkInterface ni=(NetworkInterface)interfaces.nextElement();
                Enumeration ipAddrEnum = ni.getInetAddresses();
                while(ipAddrEnum.hasMoreElements()) {

                    InetAddress addr = (InetAddress)ipAddrEnum.nextElement();

                    if (addr.isLoopbackAddress()) {
                        continue;
                    }

                    String ip = addr.getHostAddress();

                    if (ip.contains(":")) {
                        //skip the IPv6 addr
                        continue;
                    }

                    log.debug("Interface: " + ni.getName() + ", IP: " + ip);
                    ipList.add(ip);
                }
            }

            Collections.sort(ipList);
        } catch (Exception e) {

            e.printStackTrace();
            log.error("Failed to get local ip list. " + e.getMessage());
            throw new RuntimeException("Failed to get local ip list");

        }

        return ipList;
    }


    public static void getLocalIpAddr(Set<String> set) {
        ArrayList<String> addrList = getLocalIpAddr();
        set.clear();
        set.addAll(addrList);
    }



    public static void main(String args[]) throws UnknownHostException {

        //ArrayList<String> addrList = getLocalIpAddr();

        HashSet<String> addrSet = new HashSet<>();
        getLocalIpAddr(addrSet);

        for (String ip : addrSet) {
            System.out.println("Local ip:" + ip);
        }

        System.out.println(InetAddress.getLocalHost());
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }

}
