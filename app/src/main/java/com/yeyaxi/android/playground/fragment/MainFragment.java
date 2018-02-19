package com.yeyaxi.android.playground.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yeyaxi.android.playground.DynamicScalingLinearLayoutManager;
import com.yeyaxi.android.playground.R;
import com.yeyaxi.android.playground.adapter.PostsAdapter;
import com.yeyaxi.android.playground.api.ApiClient;
import com.yeyaxi.android.playground.constant.Params;
import com.yeyaxi.android.playground.decorator.ViewDecorator;
import com.yeyaxi.android.playground.interfaces.OnPostClick;
import com.yeyaxi.android.playground.model.Post;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MainFragment extends Fragment implements OnPostClick {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;

    private ApiClient apiClient;
    private PostsAdapter adapter;
    private List<Post> posts = new ArrayList<>();

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
        this.adapter.setDataSource(this.posts);

        setupLayoutManager();
        setupItemDecoration();
        this.recyclerView.setAdapter(this.adapter);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Observable<List<Post>> postsObservable = this.apiClient.getApiInterface().getPosts();
        postsObservable
                .flatMap(Observable::fromIterable)
                .flatMap(post -> {
                    Long userId = post.getUserId();
                    return this.apiClient.getApiInterface().getUser(userId)
                            .map(user -> {
                                post.setUser(user);
                                return post;
                            });
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        post -> {
                            this.posts.add(post);
                            this.adapter.notifyItemInserted(this.posts.size() - 1);
                        },
                        throwable -> showError(),
                        () -> {});
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(getContext(), "Search selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onPostClick(Post post) {
        Bundle args = new Bundle();
        args.putLong(Params.PARAM_POST_ID, post.getId());
        args.putLong(Params.PARAM_USER_ID, post.getUserId());
        DetailFragment fragment = DetailFragment.newInstance(args);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("detail")
                .replace(R.id.container, fragment)
                .commit();
    }

    private void setupItemDecoration() {
        int vPadding = getResources().getDimensionPixelSize(R.dimen.carousel_vertical);
        int hPadding = getResources().getDimensionPixelSize(R.dimen.carousel_horizontal);
        this.recyclerView.addItemDecoration(new ViewDecorator(hPadding, vPadding));
    }

    private void setupLayoutManager() {
        DynamicScalingLinearLayoutManager llm = new DynamicScalingLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        SnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(this.recyclerView);
        this.recyclerView.setLayoutManager(llm);
    }

    private void showError() {
        if (!isResumed()) {
            return;
        }
        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
    }


}
