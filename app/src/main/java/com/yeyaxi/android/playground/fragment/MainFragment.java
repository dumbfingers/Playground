package com.yeyaxi.android.playground.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yeyaxi.android.playground.interfaces.OnPostClick;
import com.yeyaxi.android.playground.constant.Params;
import com.yeyaxi.android.playground.adapter.PostsAdapter;
import com.yeyaxi.android.playground.R;
import com.yeyaxi.android.playground.api.ApiClient;
import com.yeyaxi.android.playground.model.Post;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class MainFragment extends Fragment implements OnPostClick {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;

    private ApiClient apiClient;
    private PostsAdapter adapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        this.unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.apiClient = new ApiClient();
        this.adapter = new PostsAdapter();
        this.adapter.setDelegate(this);

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        this.recyclerView.setLayoutManager(llm);
        this.recyclerView.setAdapter(this.adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Observable<List<Post>> postsObservable = this.apiClient.getApiInterface().getPosts();
        postsObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Post>>() {
                    @Override
                    public void onNext(List<Post> posts) {
                        adapter.setDataSource(posts);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.unbinder.unbind();
    }

    @Override
    public void onPostClick(Long postId) {
        Bundle args = new Bundle();
        args.putLong(Params.PARAM_POST_ID, postId);
        DetailFragment fragment = DetailFragment.newInstance(args);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("detail")
                .replace(R.id.container, fragment)
                .commit();
    }
}
