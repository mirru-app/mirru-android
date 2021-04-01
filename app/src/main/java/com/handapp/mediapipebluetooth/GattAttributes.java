/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.handapp.mediapipebluetooth;

import java.util.HashMap;

public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String ARDUINO_SERVICE = "0000180d-b5a3-f393-e0a9-e50e24dcca9e";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002a37-b5a3-f393-e0a9-e50e24dcca9e";

    static {
        attributes.put(ARDUINO_SERVICE, "Arduino Service");
        attributes.put(CLIENT_CHARACTERISTIC_CONFIG, "Bluetooth Characteristic");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}