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
import WrapordererList from './OrdererList';
import WrapSettingPeer from './settingOrderer';
import styles from '../styles.less';
import peer from '@/assets/节点.png';

const FormItem = Form.Item;
const Option = Select.Option;
const TabPane = Tabs.TabPane;

@connect(({ ordererManager }) => {
  return {
    ordererManager
  };
})
export default class ordererManager extends React.Component {
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
        type: 'ordererManager/handleResetUpdate'
      });
    }
  }

  render() {
    const { activeKey } = this.state;
    const { ordererManager } = this.props;
    const detailInfo = (
      <div className={styles.peer}>Orderer节点管理</div>
    );

    return (
      <PageHeaderLayout detailInfo={detailInfo} logo={peer}>
        <Tabs onChange={this.handleChangeTabs} activeKey={activeKey} className={styles.tabs}>
          <TabPane
            key="1"
            className={styles.tabChildren}
            tab={<span><Icon type="file-text" />节点列表</span>}
          >
            <TabPaneCon children={<WrapordererList onChangeTab={() => this.handleChangeTabs('2')} />} title="orderer节点列表" />
          </TabPane>
          <TabPane
            key="2"
            className={styles.tabChildren}
            tab={<span><Icon type="file-text" />配置节点</span>}
          >
            <TabPaneCon children={<WrapSettingPeer />} title={ordererManager.isUpdate ? "更新节点" : "配置节点"} />
          </TabPane>
        </Tabs>
      </PageHeaderLayout>
    )
  }
}
