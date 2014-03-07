/*******************************************************************************
 * Copyright 2012-present Pixate, Inc.                                     
 *                                                                         
 * Licensed under the Apache License, Version 2.0 (the "License");         
 * you may not use this file except in compliance with the License.        
 * You may obtain a copy of the License at                                 
 *                                                                         
 *    http://www.apache.org/licenses/LICENSE-2.0                           
 *                                                                         
 * Unless required by applicable law or agreed to in writing, software     
 * distributed under the License is distributed on an "AS IS" BASIS,       
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and     
 * limitations under the License.
 ******************************************************************************/
package com.pixate.freestyle.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.pixate.freestyle.R;

/**
 * The fragment that displays forms and buttons
 */
public class ButtonsFragment extends Fragment {

    /** Seek bar */
    SeekBar seekVolum1;
    // SeekBar seekVolum2;

    /** Volumn level - set 1 as default */
    int m_nAlarmVolum = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buttons, null);

        seekVolum1 = (SeekBar) view.findViewById(R.id.seekVolum1);
        // seekVolum2 = (SeekBar)view.findViewById(R.id.seekVolum2);

        view.findViewById(R.id.txtLeft).setOnClickListener(mClickListener);
        view.findViewById(R.id.txtRight).setOnClickListener(mClickListener);

        setSeekBar(seekVolum1);
        // setSeekBar(seekVolum2);

        return view;
    }

    /** Set the volumn controling to seek bar */
    private void setSeekBar(SeekBar seek) {
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(
                Context.AUDIO_SERVICE);
        seek.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        seek.setProgress(m_nAlarmVolum);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean arg2) {
                m_nAlarmVolum = progress;

                AudioManager audioManager = (AudioManager) getActivity().getSystemService(
                        Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, m_nAlarmVolum,
                        AudioManager.FLAG_ALLOW_RINGER_MODES | AudioManager.FLAG_PLAY_SOUND);
            }
        });
    }

    /** Defined view click listener for buttons and texts */
    View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.txtLeft:
                    break;
                case R.id.txtRight:
                    break;
            }
        }

    };
}
