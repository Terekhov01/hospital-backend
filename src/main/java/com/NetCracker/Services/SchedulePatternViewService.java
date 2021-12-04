package com.NetCracker.Services;

import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class SchedulePatternViewService
{
    @Autowired
    ScheduleService scheduleService;

    @Autowired
    SchedulePatternService schedulePatternService;

    public String getSchedulePatternList()
    {
        List<SchedulePattern> schedulePatternList = schedulePatternService.getPatternsByDoctor();

        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(SchedulePattern.class, new JsonSerializer<SchedulePattern>()
        {
            @Override
            public JsonElement serialize(SchedulePattern pattern, Type type, JsonSerializationContext jsonDeserializationContext) throws JsonParseException
            {
                JsonObject patternObject = new JsonObject();
                patternObject.add("id", new JsonPrimitive(pattern.getId()));
                patternObject.add("name", new JsonPrimitive(pattern.getName()));
                patternObject.add("daysLength", new JsonPrimitive(pattern.getDaysLength()));

                return patternObject;
            }
        });

        Gson gson = gsonBuilder.create();

        return gson.toJson(schedulePatternList);
    }
}
