package com.example.demoexport;

import org.springframework.batch.core.listener.StepListenerSupport;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class SysOutItemWriter extends StepListenerSupport implements ItemWriter<StickerInfo> {

    @Override
    public void write(List<? extends StickerInfo> items) throws Exception {
        System.out.println("The size of the chuck is " + items.size());
        for (StickerInfo item: items){
            System.out.println(">> "+ item);
        }
    }
}
