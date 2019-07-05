import React from 'react';
import { connect } from 'dva';
import {
  Icon,
  Button,
  Form,
  Tabs,
  Select,
  message,
  Input,
} from 'antd';
import PageHeaderLayout from '@/components/PageHeaderWrapper';
import TabPaneCon from '@/components/TabPaneCon';
import WrapPeerList from './PeerList';
import WrapSettingPeer from './settingPeer';
import styles from '../styles.less';
import peer from '@/assets/节点.png';

const FormItem = Form.Item;
const Option = Select.Option;
const TabPane = Tabs.TabPane;

@connect(({ peerManager }) => {
  return {
    peerManager
  };
})
export default class PeerManager extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      activeKey: '1',
    };
  }

  handleChangeTabs = (key) => {
    const { dispatch } = this.props;
    this.setState({
      activeKey: key
    });
    if (key === '1') {
      dispatch({
        type: 'peerManager/handleResetUpdate'
      });
    }
  }

  render() {
    const { activeKey } = this.state;
    const { peerManager } = this.props;
    const detailInfo = (
      <div className={styles.peer}>Peer节点管理</div>
    );

    return (
      <PageHeaderLayout detailInfo={detailInfo} logo={peer}>
        <Tabs onChange={this.handleChangeTabs} activeKey={activeKey} className={styles.tabs}>
          <TabPane
            key="1"
            className={styles.tabChildren}
            tab={<span><Icon type="file-text" />节点列表</span>}
          >
            <TabPaneCon children={<WrapPeerList onChangeTab={() => this.handleChangeTabs('2')} />} title="Peer节点列表" />
          </TabPane>
          <TabPane
            key="2"
            className={styles.tabChildren}
            tab={<span><Icon type="file-text" />配置节点</span>}
          >
            <TabPaneCon children={<WrapSettingPeer />} title={peerManager.isUpdate ? "更新节点" : "配置节点"} />
          </TabPane>
        </Tabs>
      </PageHeaderLayout>
    )
  }
}
