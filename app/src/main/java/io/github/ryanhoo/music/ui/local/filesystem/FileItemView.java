package io.github.ryanhoo.music.ui.local.filesystem;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.ui.base.adapter.IAdapterView;
import io.github.ryanhoo.music.utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 11:03 PM
 * Desc: FilItemView
 */
public class FileItemView extends RelativeLayout implements IAdapterView<File> {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static SimpleDateFormat DATE_FORMATTER;

    @BindView(R.id.image_button_file)
    ImageButton imageButtonFile;
    @BindView(R.id.text_view_name)
    TextView textViewName;
    @BindView(R.id.text_view_info)
    TextView textViewInfo;
    @BindView(R.id.text_view_date)
    TextView textViewDate;

    static {
        DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    }

    public FileItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.item_local_file, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bind(File file, int position) {
        textViewName.setText(file.getName());
        if (file.isDirectory()) {
            imageButtonFile.setImageResource(R.drawable.ic_folder);
            File[] files = file.listFiles(SystemFileFilter.DEFAULT_INSTANCE);
            int itemCount = files == null ? 0 : files.length;
            textViewInfo.setText(getContext().getResources().getQuantityString(
                    R.plurals.mp_directory_items_formatter,
                    itemCount, // zero
                    itemCount, // one
                    itemCount  // other
            ));
        } else {
            imageButtonFile.setImageResource(R.drawable.ic_file);
            textViewInfo.setText(FileUtils.readableFileSize(file.length()));
        }
        Date lastModified = new Date(file.lastModified());
        textViewDate.setText(DATE_FORMATTER.format(lastModified));
    }
}
