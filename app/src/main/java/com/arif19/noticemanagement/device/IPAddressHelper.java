package com.arif19.noticemanagement.device;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IPAddressHelper {

    public static String getIPAddress() {
        try {
            // Get all network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();

                // Ignore loopback, non-up interfaces, and virtual interfaces
                if (iface.isLoopback() || !iface.isUp() || iface.isVirtual())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // Check for IPv4 address
                    if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(':') == -1) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null; // Return null if no suitable IP address is found
    }
}
