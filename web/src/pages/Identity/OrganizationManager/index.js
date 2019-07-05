import React, { Component } from 'react';
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
import WrapOrgList from './orgList';
import WrapSettingOrg from './settingOrg';
import CreateCert from './createCert.js';
import styles from '../styles.less';
import peer from '@/assets/节点.png';

const FormItem = Form.Item;
const Option = Select.Option;
const TabPane = Tabs.TabPane;

@connect(({ organizationManager }) => {
  return {
    organizationManager
  };
})
export default class OrganizationManager extends Component {
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
        type: 'organizationManager/handleResetUpdate'
      });
    }
  }

  render() {
    const { activeKey } = this.state;
    const { organizationManager } = this.props;
    const detailInfo = (
      <div className={styles.peer}>组织管理</div>
    );

    return (
      <PageHeaderLayout detailInfo={detailInfo} logo={peer}>
        <Tabs onChange={this.handleChangeTabs} activeKey={activeKey} className={styles.tabs}>
          <TabPane
            key="1"
            className={styles.tabChildren}
            tab={<span><Icon type="file-text" />组织列表</span>}
          >
            <TabPaneCon children={<WrapOrgList onChangeTab={() => this.handleChangeTabs('2')} />} title="组织列表" />
          </TabPane>
          <TabPane
            key="2"
            className={styles.tabChildren}
            tab={<span><Icon type="file-text" />配置组织</span>}
          >
            <TabPaneCon children={<WrapSettingOrg />} title={organizationManager.isUpdate ? "更新组织" : "新增组织"} />
          </TabPane>
          <TabPane
            key="3"
            className={styles.tabChildren}
            tab={<span><Icon type="file-text" />生成证书</span>}
          >
            <TabPaneCon children={<CreateCert />} title="生成证书" />
          </TabPane>
        </Tabs>
      </PageHeaderLayout>
    )
  }
}
