package io.github.ryanhoo.music.ui.local.folder;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.data.model.Folder;
import io.github.ryanhoo.music.ui.base.adapter.IAdapterView;

import java.util.Random;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 7:22 PM
 * Desc: AddedFolderItemView
 */
public class AddedFolderItemView extends RelativeLayout implements IAdapterView<Folder> {

    @BindView(R.id.text_view_name)
    TextView textViewName;
    @BindView(R.id.text_view_info)
    TextView textViewInfo;

    public AddedFolderItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.item_added_folder, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bind(Folder folder, int position) {
        textViewName.setText(folder.getName());
        textViewInfo.setText(getContext().getString(
                R.string.mp_local_files_folder_list_item_info_formatter,
                new Random().nextInt(1000),
                folder.getPath()
        ));
    }

    @OnClick(R.id.image_button_action)
    public void onItemAction(View view) {

    }
}
