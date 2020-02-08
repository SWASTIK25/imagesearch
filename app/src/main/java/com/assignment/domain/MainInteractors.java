package com.assignment.domain;

import androidx.annotation.NonNull;

import com.assignment.data.PhotoSearchRepository;
import com.assignment.data.SearchPhotoDataModel;
import com.assignment.data.SearchPhotoRequest;

import javax.inject.Inject;

import io.reactivex.Observable;

public class MainInteractors {

    public static class PhotoSearch implements ReactiveInteractor.RetrieveInteractor<SearchPhotoRequest, SearchPhotoDataModel> {


        private final PhotoSearchRepository mPhotoSearchRepository;

        @Inject
        public PhotoSearch(PhotoSearchRepository photoSearchRepository) {
            this.mPhotoSearchRepository = photoSearchRepository;
        }

        @NonNull
        @Override
        public Observable<SearchPhotoDataModel> getBehaviorStream(@NonNull SearchPhotoRequest searchPhotoRequest) {
            return mPhotoSearchRepository.searchPhotos(searchPhotoRequest).toObservable();
        }

        public Observable<Boolean> storeDataLocally(@NonNull String query,SearchPhotoDataModel searchPhotoDataModel) {
            return mPhotoSearchRepository.getFileInDirectoryAndStoreData(query, searchPhotoDataModel.getPhotos().getPhoto());
        }
    }
}
