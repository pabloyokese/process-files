package com.example.demoexport;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Data;


@Data
public class StickerInfo {
    @XStreamAsAttribute
    @XStreamAlias("sid")
    private String labelId;

    @XStreamAlias("tid")
    private String hiddenCode;

    @XStreamAlias("Batch")
    private String batch;

    @XStreamAlias("Reel")
    private String spoolNo;

    @XStreamAlias("Version")
    private String projectNumber;

    @XStreamAlias("Project")
    private String type;

    @XStreamAlias("Type")
    private String version;

    @XStreamAlias("Supplier")
    private String supplier;

}
