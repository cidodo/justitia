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
import WrapUserList from './userList';
import WrapSettingUser from './settingUser';
import styles from '../styles.less';
import peer from '@/assets/节点.png';

const FormItem = Form.Item;
const Option = Select.Option;
const TabPane = Tabs.TabPane;

@connect(({ fabricUserManager }) => {
  return {
    fabricUserManager
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
        type: 'fabricUserManager/handleResetUpdate'
      });
    }
  }

  render() {
    const { activeKey } = this.state;
    const { fabricUserManager } = this.props;
    const detailInfo = (
      <div className={styles.peer}>用户管理</div>
    );

    return (
      <PageHeaderLayout detailInfo={detailInfo} logo={peer}>
        <Tabs onChange={this.handleChangeTabs} activeKey={activeKey} className={styles.tabs}>
          <TabPane
            key="1"
            className={styles.tabChildren}
            tab={<span><Icon type="file-text" />用户列表</span>}
          >
            <TabPaneCon children={<WrapUserList onChangeTab={() => this.handleChangeTabs('2')} />} title="用户列表" />
          </TabPane>
          <TabPane
            key="2"
            className={styles.tabChildren}
            tab={<span><Icon type="file-text" />配置用户</span>}
          >
            <TabPaneCon children={<WrapSettingUser />} title={fabricUserManager.isUpdate ? "更新用户" : "新增用户"} />
          </TabPane>
        </Tabs>
      </PageHeaderLayout>
    )
  }
}
