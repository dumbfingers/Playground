package com.yeyaxi.android.playground.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yeyaxi.android.playground.DynamicScalingLinearLayoutManager;
import com.yeyaxi.android.playground.R;
import com.yeyaxi.android.playground.adapter.PostsAdapter;
import com.yeyaxi.android.playground.api.ApiClient;
import com.yeyaxi.android.playground.constant.Params;
import com.yeyaxi.android.playground.decorator.ViewDecorator;
import com.yeyaxi.android.playground.interfaces.OnPostClick;
import com.yeyaxi.android.playground.model.Post;
import com.yeyaxi.android.playground.util.AvatarUriUtil;

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
    @BindView(R.id.background)
    AppCompatImageView background;
    @BindView(R.id.index_indicator)
    TextView indexIndicator;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

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
        setHasOptionsMenu(true);

        this.apiClient = new ApiClient();
        this.adapter = new PostsAdapter();
        this.adapter.setDelegate(this);
        this.adapter.setDataSource(this.posts);

        setupLayoutManager();
        setupItemDecoration();
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int index = layoutManager.findFirstCompletelyVisibleItemPosition();
                updateBackgrounImage(index);
                updateIndicator(index, posts.size());
            }
        });
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
                        () -> updateIndicator(0, this.posts.size()));
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(this.toolbar);
    }

    @Override
    public void onPostClick(View view, Post post) {
        Bundle args = new Bundle();
        args.putLong(Params.PARAM_POST_ID, post.getId());
        args.putLong(Params.PARAM_USER_ID, post.getUserId());
        DetailFragment fragment = DetailFragment.newInstance(args);
        fragment.setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.change_transform));
        fragment.setEnterTransition(new Explode());
        fragment.setExitTransition(new Explode());
        fragment.setSharedElementReturnTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.change_transform));
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(view, view.getTransitionName())
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

    private void updateBackgrounImage(int index) {
        if (index < 0 || !isResumed()) {
            return;
        }
        Post post = this.posts.get(index);
        String uri = AvatarUriUtil.getAvatarUri(post.getUser().getEmail());
        Picasso.with(getContext()).load(uri).into(this.background);
    }

    private void updateIndicator(int current, int total) {
        if (current < 0 || !isResumed()) {
            return;
        }
        this.indexIndicator.setText(getString(R.string.index_counter, current + 1, total));
    }
}
