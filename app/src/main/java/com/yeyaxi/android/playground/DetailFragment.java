package com.yeyaxi.android.playground;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yeyaxi.android.playground.model.Comment;
import com.yeyaxi.android.playground.model.Post;
import com.yeyaxi.android.playground.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class DetailFragment extends Fragment {

    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.comments_count)
    TextView commentsCount;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.body)
    TextView body;
    Unbinder unbinder;

    private ApiClient apiClient;

    public static DetailFragment newInstance(Bundle  extra) {
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(extra);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_post_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        this.apiClient = new ApiClient();

        if (args != null) {
            Long postId = args.getLong(Params.PARAM_POST_ID);

            Observable<List<Comment>> commentObservable = this.apiClient.getApiInterface().getComments(postId);
            commentObservable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<List<Comment>>() {
                        @Override
                        public void onNext(List<Comment> comments) {
                            fillCommentCount(comments.size());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });


            Observable<Post> postObservable = this.apiClient.getApiInterface().getPost(postId);
            postObservable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Post>() {
                        @Override
                        public void onNext(Post post) {
                            fillView(post);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void fillView(Post post) {
        this.title.setText(post.getTitle());
        this.body.setText(post.getBody());

        Long userId = post.getUserId();
        Observable<User> userObservable = this.apiClient.getApiInterface().getUser(userId);
        userObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<User>() {
                    @Override
                    public void onNext(User user) {
                        if (isResumed()) {
                            fillUser(user);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void fillUser(User user) {
        this.userName.setText(user.getName());
    }

    private void fillCommentCount(int size) {
        this.commentsCount.setText(String.format(getResources().getQuantityString(R.plurals.plural_comment, size), size));
    }
}

