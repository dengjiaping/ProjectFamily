package com.hmammon.photointerface.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Created by Xcfh on 2014/10/16.
 */
public abstract class Entity<T extends Entity> {
    public abstract JSONObject beJson() throws JSONException;
    public abstract T beObject(JSONObject json) throws IllegalArgumentException;
}
