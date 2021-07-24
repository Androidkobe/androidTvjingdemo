package com.partybuild.boxindex_cmcc;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MacUtils {

    private static final String TAG = "MacUtil";

    private MacUtils() {
    }

    /**
     * 获取当前系统连接网络的网卡的mac地址
     */
    public static final String getMac() {
        byte[] mac = null;
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces != null && netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (ip.isAnyLocalAddress() || !(ip instanceof Inet4Address) || ip.isLoopbackAddress())
                        continue;
                    if (ip.isSiteLocalAddress()) {
                        mac = ni.getHardwareAddress();
                    } else if (!ip.isLinkLocalAddress()) {
                        mac = ni.getHardwareAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return getMacString(mac);
    }

    /**
     * 获取wifi模块的mac地址
     */
    public static String getWifiMac() {
        return getNetworkInterfaceMac("wlan0");
    }


    /**
     * 获取有线网卡模块的mac地址
     */
    public static String getEthernetMac() {
        return getNetworkInterfaceMac("eth0");
    }


    /**
     * 获取指定网卡mac地址
     */
    private static String getNetworkInterfaceMac(String networkInterfaceName) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                if (networkInterfaceName.equals(ni.getName())) {
                    return getMacString(getMacBytes(ni));
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static byte[] getMacBytes(NetworkInterface ni) {
        byte[] mac = null;
        try {
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                InetAddress ip = address.nextElement();
                if (ip.isAnyLocalAddress() || !(ip instanceof Inet4Address) || ip.isLoopbackAddress())
                    continue;
                if (ip.isSiteLocalAddress())
                    mac = ni.getHardwareAddress();
                else if (!ip.isLinkLocalAddress()) {
                    mac = ni.getHardwareAddress();
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return mac;
    }

    private static String getMacString(byte[] mac) {
        if (mac != null) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mac.length; i++) {
                sb.append(parseByte(mac[i]));
            }
            return sb.substring(0, sb.length() - 1);
        }
        return "";
    }

    private static String parseByte(byte b) {
        String s = "00" + Integer.toHexString(b) + ":";
        return s.substring(s.length() - 3);
    }
}