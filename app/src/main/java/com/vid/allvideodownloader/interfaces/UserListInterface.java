package com.vid.allvideodownloader.interfaces;


import com.vid.allvideodownloader.model.FBStoryModel.NodeModel;
import com.vid.allvideodownloader.model.story.TrayModel;

public interface UserListInterface {
    void userListClick(int position, TrayModel trayModel);
    void fbUserListClick(int position, NodeModel trayModel);
}
