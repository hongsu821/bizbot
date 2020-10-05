package com.bizbot.bizbot.Room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.bizbot.bizbot.Room.DAO.PermitDAO;
import com.bizbot.bizbot.Room.DAO.SupportDAO;
import com.bizbot.bizbot.Room.Entity.PermitModel;
import com.bizbot.bizbot.Room.Entity.SupportModel;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * local에서 가져올지 network에서 가져올지 정함
 */
public class AppRepository {
    private PermitDAO mPermitDAO; //권한 DAO
    private SupportDAO mSupportDAO; //지원 사업 DAO
    private LiveData<List<SupportModel>> allSupportItem; //지원 사업 리스트
    private LiveData<List<SupportModel>> allLikedItem; //관심 사업 리스트
    private LiveData<PermitModel> alert; //알림

    public AppRepository(Application application){
        AppDatabase db = AppDatabase.getInstance(application);
        mSupportDAO = db.supportDAO(); //db에 있는 supportDAO 가져오기
        allSupportItem = mSupportDAO.getAll(); //지원 사업 아이템 전부 가져오기
        allLikedItem = mSupportDAO.getLikedList(); //관심 사업 아이템 전부 가져오기

        mPermitDAO = db.permitDAO();//db에 있는 permitDAO 가져오기
        alert = mPermitDAO.getAlertState(); //알림 설정 상태 가져오기
        db.close(); //db 종료
    }


    //지원사업 리스트 모든 리스트 출력
    public LiveData<List<SupportModel>> getAllSupportItem(){
        return allSupportItem;
    }

    public LiveData<List<SupportModel>> getAllLikedItem() {
        return allLikedItem;
    }

    //지원사업 데이터 추가
    public void insertSupportItem(SupportModel support){
        Runnable addRun = () -> mSupportDAO.insert(support);

        Executor diskIO = Executors.newSingleThreadExecutor();
        diskIO.execute(addRun);

    }

    //알림설정 상태 출력
    public LiveData<PermitModel> getAlertState() {return alert;}
    //알림설정 변경
    public void updateAlertState(PermitModel alertPermit){
        Runnable updateRun = () -> mPermitDAO.setAlert(alertPermit.isAlert());

        Executor diskIO = Executors.newSingleThreadExecutor();
        diskIO.execute(updateRun);
    }

}
